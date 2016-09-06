package in.nikhilbhardwaj.path.route.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Service {
  public abstract String serviceId();
  public abstract LocalDate startDate();
  public abstract LocalDate endDate();
  public abstract EnumSet<DayOfWeek> days();
}
