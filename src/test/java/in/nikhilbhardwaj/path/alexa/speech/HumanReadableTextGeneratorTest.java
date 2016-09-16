package in.nikhilbhardwaj.path.alexa.speech;

import static in.nikhilbhardwaj.path.route.model.StopNames.GROVE_ST;
import static in.nikhilbhardwaj.path.route.model.StopNames.WTC;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import in.nikhilbhardwaj.path.route.model.ImmutableStopTime;
import in.nikhilbhardwaj.path.route.model.ImmutableTrip;
import in.nikhilbhardwaj.path.route.model.StopNames;
import in.nikhilbhardwaj.path.route.model.StopTime;
import in.nikhilbhardwaj.path.route.model.Trip;

public class HumanReadableTextGeneratorTest {
  
  private HumanReadableTextGenerator generator = new HumanReadableTextGenerator();
  
  @Test
  public void noRouteWhenTripsEmpty() {
    String response = generator.from(ImmutableList.of());
    assertTrue(response.contains("Sorry, I couldn't find any trips for you"));
  }
  
  @Test
  public void appropriateResponseForSingleTrip() {
    String response = generator.from(ImmutableList.of(constructSampleTrip()));
    assertTrue(response.contains("The next train"));
    assertFalse(response.contains("the one after that"));
  }
  
  @Test
  public void appropriateResponseForMultipleTrips() {
    String response = generator.from(ImmutableList.of(constructSampleTrip(), constructSampleTrip()));
    assertTrue(response.contains("The next train"));
    assertTrue(response.contains("the one after that"));
  }

  private Trip constructSampleTrip() {
    return ImmutableTrip.builder()
        .routeId(randomAlphanumeric(5))
        .tripId(randomAlphanumeric(5))
        .serviceId(randomAlphanumeric(5))
        .origin(constructStopTime(WTC))
        .destination(constructStopTime(GROVE_ST))
        .build();
  }
  
  private StopTime constructStopTime(StopNames stop) {
    return ImmutableStopTime.builder()
        .stopName(stop)
        .time(LocalTime.now())
        .tripId(randomAlphanumeric(5))
        .build();
  }
}
