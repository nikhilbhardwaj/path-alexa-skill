package in.nikhilbhardwaj.path.route.model;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Trip {
  public abstract String routeId();
  public abstract String serviceId();
  public abstract String tripId();
  // Optional Attributes to be filled in once we've determined the route
  public abstract Optional<StopTime> origin();
  public abstract Optional<StopTime> destination();
}
