package in.nikhilbhardwaj.path.route.resources;

import org.junit.Test;

import static in.nikhilbhardwaj.path.route.model.StopNames.*;
import in.nikhilbhardwaj.path.route.resources.StopsResource;

import static org.junit.Assert.*;

import java.util.Optional;

public class StopsResourceTest {

  private StopsResource stopsResource = new StopsResource();

  @Test
  public void stationNamesAreResolvedFromIds() {
    assertEquals(NEWPORT, stopsResource.getStopName("781728").get());
    assertEquals(NEWPORT, stopsResource.getStopName("26732").get());
    assertEquals(Optional.empty(), stopsResource.getStopName("000"));
  }
}
