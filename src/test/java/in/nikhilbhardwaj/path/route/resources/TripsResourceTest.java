package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.StopNames.FOURTEEN_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.GROVE_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.NEWPORT;
import static in.nikhilbhardwaj.path.route.model.StopNames.WTC;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import in.nikhilbhardwaj.path.alexa.GuicyAbstractTest;
import in.nikhilbhardwaj.path.route.model.ImmutableRouteRequest;
import in.nikhilbhardwaj.path.route.model.RouteRequest;

public class TripsResourceTest extends GuicyAbstractTest {
  private TripsResource trips = GUICE.getInstance(TripsResource.class);

  @Test
  public void noTripsForInvalidService() {
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.now())
        .time(LocalTime.now())
        .origin(NEWPORT)
        .destination(GROVE_ST)
        .build();
    assertTrue(
        trips.getTripsForServiceConnectingOriginAndDestination("abc", request).isEmpty());
  }

  @Test
  public void tripsFoundForStopsNotOnService() {
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.now())
        .time(LocalTime.now())
        .origin(WTC)
        .destination(FOURTEEN_ST)
        .build();
    assertTrue(trips.getTripsForServiceConnectingOriginAndDestination("3907A5291", request)
        .isEmpty());
  }

  @Test
  public void tripsFoundForValidService() {
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.of(2016, 9, 7))
        .time(LocalTime.of(9, 30)).
        origin(NEWPORT)
        .destination(FOURTEEN_ST)
        .build();

    Set<String> tripIds = trips.getTripsForServiceConnectingOriginAndDestination("3907A5291", request)
            .stream()
            .map(trip -> trip.tripId())
            .collect(Collectors.toSet());
    Set<String> someKnownTrips = ImmutableSet.of("159406A3907B5291", "159410A3907B5291",
        "159444A3907B5291", "159425A3907B5291", "159417A3907B5291");
    assertFalse(tripIds.isEmpty());
    assertTrue(tripIds.containsAll(someKnownTrips));
  }
}
