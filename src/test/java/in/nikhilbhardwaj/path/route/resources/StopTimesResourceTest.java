package in.nikhilbhardwaj.path.route.resources;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import static in.nikhilbhardwaj.path.route.model.StopNames.*;

import in.nikhilbhardwaj.path.route.model.StopNames;
import in.nikhilbhardwaj.path.route.resources.StopTimesResource;
import in.nikhilbhardwaj.path.route.resources.StopsResource;

public class StopTimesResourceTest {
  private StopsResource stopsResource = new StopsResource();
  private StopTimesResource stopTimesResource = new StopTimesResource(stopsResource);

  @Test
  public void noStopTimesForInvalidTrip() {
    Assert.assertTrue(stopTimesResource.getStopTimesForTrip("abc").isEmpty());
  }

  @Test
  public void stopTimesFoundForValidTrip() {
    Set<StopNames> stopNames = stopTimesResource.getStopTimesForTrip("159521A3907B5291").stream()
        .map(trip -> trip.stopName())
        .collect(Collectors.toSet());
    Set<StopNames> allStopsForTrip = ImmutableSet.of(THIRTY_THIRD_ST, NEWPORT, FOURTEEN_ST, HOBOKEN,
        GROVE_ST, CHRISTOPHER_ST, TWENTY_THIRD_ST, JOURNAL_SQ, NINTH_ST);
    Assert.assertFalse(stopNames.isEmpty());
    System.out.println(stopNames);
    // Check for set equality
    Assert.assertTrue(stopNames.containsAll(allStopsForTrip));
    Assert.assertTrue(allStopsForTrip.containsAll(stopNames));
  }

}
