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
  public void holidayScheduleOnThanksgiving() {
    LocalDate thanksgiving = LocalDate.parse("20161124", PATH_DATE_FORMATTER);
    Set<String> exceptionsOnThanksgiving =
        ImmutableSet.of("3941A5283", "3529A4969", "3893A5283", "3528A4969");
    Set<String> servicesOnThanksgiving = services.getServiceIdsForDate(thanksgiving);
    assertTrue(servicesOnThanksgiving.containsAll(exceptionsOnThanksgiving));
    assertTrue(exceptionsOnThanksgiving.containsAll(servicesOnThanksgiving));
  }

  @Test
  public void regularWeekdaySchedule() {
    LocalDate regularWednesday = LocalDate.parse("20160907", PATH_DATE_FORMATTER);
    Set<String> servicesRunning = services.getServiceIdsForDate(regularWednesday);
    Set<String> expectedServices = ImmutableSet.of("3528A4969", "3941A5283");
    assertTrue(servicesRunning.containsAll(expectedServices));
    assertTrue(expectedServices.containsAll(servicesRunning));
  }

  @Test
  public void regularWeekendSchedule() {
    LocalDate regularSunday = LocalDate.parse("20161016", PATH_DATE_FORMATTER);
    Set<String> servicesRunning = services.getServiceIdsForDate(regularSunday);
    Set<String> expectedServices = ImmutableSet.of("3894A5283", "3530A4969");
    assertTrue(servicesRunning.containsAll(expectedServices));
    assertTrue(expectedServices.containsAll(servicesRunning));
  }

}
