package in.nikhilbhardwaj.path.route.model;

import java.time.LocalTime;

import org.immutables.value.Value;

@Value.Immutable
public abstract class StopTime {
  public abstract String tripId();
  public abstract LocalTime time();
  public abstract StopNames stopName();
}
