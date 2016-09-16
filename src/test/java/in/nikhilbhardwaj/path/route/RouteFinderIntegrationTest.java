package in.nikhilbhardwaj.path.route;

import static in.nikhilbhardwaj.path.route.model.StopNames.GROVE_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.HARRISON;
import static in.nikhilbhardwaj.path.route.model.StopNames.NEWPORT;
import static in.nikhilbhardwaj.path.route.model.StopNames.TWENTY_THIRD_ST;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Lists;

import in.nikhilbhardwaj.path.alexa.GuicyAbstractTest;
import in.nikhilbhardwaj.path.route.model.ImmutableRouteRequest;
import in.nikhilbhardwaj.path.route.model.RouteRequest;
import in.nikhilbhardwaj.path.route.model.Trip;
import in.nikhilbhardwaj.path.route.resources.ServicesResource;
import in.nikhilbhardwaj.path.route.resources.TripsResource;

/**
 * This is a good place to start if you want to see how the resources interact with each other These
 * tests are tightly coupled with the data in the resources directory and will likely break when a
 * new copy of the path gtfs data is released and applied over.
 */
public class RouteFinderIntegrationTest extends GuicyAbstractTest {
  private ServicesResource services = GUICE.getInstance(ServicesResource.class);
  private TripsResource trips = GUICE.getInstance(TripsResource.class);

  @Test
  public void popoCommute() {
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.of(2016, 9, 6))
        .time(LocalTime.of(9, 30))
        .origin(NEWPORT)
        .destination(TWENTY_THIRD_ST)
        .build();

    Set<String> servicesRunningToday = services.getServiceIdsForDate(request.date());
    List<Trip> tripsBetweenOriginAndDestination =
        servicesRunningToday.stream()
            .map(service -> trips.getTripsForServiceConnectingOriginAndDestination(service, request))
            .flatMap(allTrips -> allTrips.stream())
            .collect(Collectors.toList());

    List<LocalTime> timesForNext3Trains = tripsBetweenOriginAndDestination.stream()
        .limit(3)
        .map(trip -> trip.origin().get().time())
        .collect(Collectors.toList());

    List<LocalTime> expectedTimesForNext3Trains =
        Lists.newArrayList(LocalTime.of(9, 32), LocalTime.of(9, 37), LocalTime.of(9, 42));

    assertFalse(timesForNext3Trains.isEmpty());
    assertArrayEquals(expectedTimesForNext3Trains.toArray(), timesForNext3Trains.toArray());
  }

  @Test
  public void weekendSchedule() {
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.of(2016, 9, 10))
        .time(LocalTime.of(9, 30)).
        origin(GROVE_ST)
        .destination(HARRISON)
        .build();

    Set<String> servicesRunningToday = services.getServiceIdsForDate(request.date());
    List<Trip> tripsBetweenOriginAndDestination =
        servicesRunningToday.stream()
            .map(service -> trips.getTripsForServiceConnectingOriginAndDestination(service, request))
            .flatMap(allTrips -> allTrips.stream())
            .collect(Collectors.toList());

    List<LocalTime> timesForNext3Trains = tripsBetweenOriginAndDestination.stream()
        .limit(3)
        .map(trip -> trip.origin().get().time())
        .collect(Collectors.toList());

    List<LocalTime> expectedTimesForNext3Trains =
        Lists.newArrayList(LocalTime.of(9, 34), LocalTime.of(9, 54), LocalTime.of(10, 14));

    assertFalse(timesForNext3Trains.isEmpty());
    assertArrayEquals(expectedTimesForNext3Trains.toArray(), timesForNext3Trains.toArray());
  }

}
