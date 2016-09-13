package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.StopNames.CHRISTOPHER_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.FOURTEEN_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.GROVE_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.HOBOKEN;
import static in.nikhilbhardwaj.path.route.model.StopNames.JOURNAL_SQ;
import static in.nikhilbhardwaj.path.route.model.StopNames.NEWPORT;
import static in.nikhilbhardwaj.path.route.model.StopNames.NINTH_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.THIRTY_THIRD_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.TWENTY_THIRD_ST;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import in.nikhilbhardwaj.path.alexa.GuicyAbstractTest;
import in.nikhilbhardwaj.path.route.model.StopNames;

public class StopTimesResourceTest extends GuicyAbstractTest {
  private StopTimesResource stopTimesResource = GUICE.getInstance(StopTimesResource.class);

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
