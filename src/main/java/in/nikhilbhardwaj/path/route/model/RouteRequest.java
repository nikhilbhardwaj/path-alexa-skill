package in.nikhilbhardwaj.path.route.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RouteRequest {
  public abstract LocalDate date();
  public abstract LocalTime time();
  public abstract StopNames origin();
  public abstract StopNames destination();
}
