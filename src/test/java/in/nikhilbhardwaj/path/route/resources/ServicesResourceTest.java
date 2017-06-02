package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.PATH_DATE_FORMATTER;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import in.nikhilbhardwaj.path.alexa.GuicyAbstractTest;

public class ServicesResourceTest extends GuicyAbstractTest {
  private ServicesResource services = GUICE.getInstance(ServicesResource.class);

  @Test
  public void holidaySchedule() {
    LocalDate memorialDay = LocalDate.parse("20170529", PATH_DATE_FORMATTER);
    Set<String> exceptionsOnHoliday = ImmutableSet.of("5394A6507", "5395A6507");
    Set<String> servicesOnHoliday = services.getServiceIdsForDate(memorialDay);
    assertTrue(servicesOnHoliday.containsAll(exceptionsOnHoliday));
    assertTrue(exceptionsOnHoliday.containsAll(servicesOnHoliday));
  }

  @Test
  public void regularWeekdaySchedule() {
    LocalDate regularWednesday = LocalDate.parse("20170602", PATH_DATE_FORMATTER);
    Set<String> servicesRunning = services.getServiceIdsForDate(regularWednesday);
    Set<String> expectedServices = ImmutableSet.of("5302A6349", "5394A6507");
    assertTrue(servicesRunning.containsAll(expectedServices));
    assertTrue(expectedServices.containsAll(servicesRunning));
  }

  @Test
  public void regularWeekendSchedule() {
    LocalDate regularSunday = LocalDate.parse("20170604", PATH_DATE_FORMATTER);
    Set<String> servicesRunning = services.getServiceIdsForDate(regularSunday);
    Set<String> expectedServices = ImmutableSet.of("5396A6507", "5302A6349");
    assertTrue(servicesRunning.containsAll(expectedServices));
    assertTrue(expectedServices.containsAll(servicesRunning));
  }

}
