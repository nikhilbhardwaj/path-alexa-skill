package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.ROUTE_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.SERVICE_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.TRIP_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCES_ROOT;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_TRIPS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import in.nikhilbhardwaj.path.route.model.ImmutableTrip;
import in.nikhilbhardwaj.path.route.model.RouteRequest;
import in.nikhilbhardwaj.path.route.model.StopNames;
import in.nikhilbhardwaj.path.route.model.StopTime;
import in.nikhilbhardwaj.path.route.model.Trip;

/**
 * The Trips resource is an abstraction built on top of the trips.txt This uses the
 * {@link StopTimesResource} to augment the trip details along with the information from StopTimes
 * corresponding to the user query. Note that the augmented information doesn't correctly reflect
 * the details of the trip but that of the origin and destination from the users request.
 * 
 * This resource does the bulk of route finding.
 */
public class TripsResource {
  private List<Trip> trips;
  private StopTimesResource stopTimes;

  /**
   * @param serviceId
   * @param routeRequest
   * @return a list of trips which are sorted by the proximity of the time at which the next train
   *         arrives at the origin station based on the users request.
   */
  public List<Trip> getTripsForServiceConnectingOriginAndDestination(String serviceId,
      RouteRequest routeRequest) {
    Set<Trip> connectingTrips = trips.stream()
        .filter(trip -> trip.serviceId().equalsIgnoreCase(serviceId))
        .filter(doesTripConnectOriginAndDestination(routeRequest.origin(), routeRequest.destination()))
        .collect(Collectors.toSet());

    return filterTripsBasedOnTime(augmentTripsWithRouteInformation(connectingTrips, routeRequest)
      , routeRequest.time());
  }

  public TripsResource(StopTimesResource stopTimes) {
    try {
      this.stopTimes = stopTimes;
      initializeTrips();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize the Trips resource.", e);
    }
  }

  private List<Trip> filterTripsBasedOnTime(Set<Trip> trips, LocalTime time) {
    return trips.stream()
        // Filter out trips which have already passed the origin stop when the request was made
        .filter(trip -> trip.origin().get().time().isAfter(time))
        // Sort on time the train starts from the origin
        .sorted((trip1, trip2) -> trip1.origin().get().time()
          .compareTo(trip2.origin().get().time()))
        .collect(Collectors.toList());
  }

  // Adds the origin and destination StopTimes to the trips
  private Set<Trip> augmentTripsWithRouteInformation(Set<Trip> trips, RouteRequest routeRequest) {
    return trips.stream()
        .map(trip -> ImmutableTrip.copyOf(trip)
            .withOrigin(getStopTimeForStopName(routeRequest.origin(), trip).get())
            .withDestination(getStopTimeForStopName(routeRequest.destination(), trip).get()))
        .collect(Collectors.toSet());
  }


  // Predicate to filter out trips that connect the given origin and destination
  private Predicate<? super Trip> doesTripConnectOriginAndDestination(StopNames origin,
      StopNames destination) {
    return trip -> {
      Optional<StopTime> stopForOrigin = getStopTimeForStopName(origin, trip);
      Optional<StopTime> stopForDestination = getStopTimeForStopName(destination, trip);
      if (stopForOrigin.isPresent() && stopForDestination.isPresent()) {
        return stopForDestination.get().time()
            .isAfter(stopForOrigin.get().time());
      }
      return false;
    };
  }

  private Optional<StopTime> getStopTimeForStopName(StopNames stopName, Trip trip) {
    Set<StopTime> stopTimesOnTrip = stopTimes.getStopTimesForTrip(trip.tripId());
    return stopTimesOnTrip.stream()
        .filter(s -> s.stopName() == stopName)
        .findFirst();
  }

  private void initializeTrips() throws FileNotFoundException, IOException {
    trips = new ArrayList<>();
    Iterable<CSVRecord> records = CSVFormat.RFC4180
        .withFirstRecordAsHeader()
        .parse(new FileReader(RESOURCES_ROOT + RESOURCE_TRIPS));
    for (CSVRecord record : records) {
      trips.add(ImmutableTrip.builder()
        .tripId(record.get(TRIP_ID))
        .routeId(record.get(ROUTE_ID))
        .serviceId(record.get(SERVICE_ID))
        .build());
    }
  }
}
