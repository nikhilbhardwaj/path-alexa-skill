package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.PATH_DATE_FORMATTER;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.DATE;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.END_DATE;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.SERVICE_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.START_DATE;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_CALENDAR;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_CALENDAR_DATES;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import in.nikhilbhardwaj.path.route.model.ImmutableService;
import in.nikhilbhardwaj.path.route.model.Service;

/**
 * The Services resource is an abstraction built on top of the calendar.txt and calendar_dates.txt
 * This returns the valid services that are running on a given date, calendar.txt has the regular
 * schedule and the exceptions are applied from calendar_dates to account for weekends or for
 * holidays when the weekend schedule is active.
 */
@Singleton
public final class ServicesResource {
  private TransitDataRepository transitData;
  private Set<Service> services;
  // The schedule exceptions generally corresponds to holidays
  // or special events i.e. on new years eve the the trains run
  // on weekday frequency to accomodate people heading in and out of the city.
  // Not to be confused with Java's exceptions and exception handling
  private Multimap<LocalDate, String> exceptions;

  // 1. if there are exceptions for a date, simply return those
  // 2. otherwise find out all active service ids for a given date
  // and filter out based on the date being requested being a weekday or a weekend
  public Set<String> getServiceIdsForDate(LocalDate date) {
    if (exceptions.containsKey(date)) {
      return ImmutableSet.copyOf(exceptions.get(date));
    }

    return ImmutableSet.copyOf(services.stream()
      .filter(isServiceActiveOnDate(date))
      .map(service -> service.serviceId())
      .collect(Collectors.toSet()));
  }

  @Inject
  public ServicesResource(TransitDataRepository transitData) {
    this.transitData = transitData;
    initializeServiceIds();
    initializeExceptions();
  }

  private void initializeExceptions() {
    exceptions = ArrayListMultimap.create();
    try (InputStreamReader exceptionsReader = new InputStreamReader(transitData.forResource(RESOURCE_CALENDAR_DATES), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
          .parse(exceptionsReader);
      for (CSVRecord record : records) {
        exceptions.put(LocalDate.parse(record.get(DATE), PATH_DATE_FORMATTER),
          record.get(SERVICE_ID));
      }  
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize ServicesResource.", e);
    }
  }

  private void initializeServiceIds() {
    services = new HashSet<>();
    try (InputStreamReader servicesReader = new InputStreamReader(transitData.forResource(RESOURCE_CALENDAR), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
          .parse(servicesReader);
      for (CSVRecord record : records) {
        services.add(ImmutableService.builder()
          .serviceId(record.get(SERVICE_ID))
          .startDate(LocalDate.parse(record.get(START_DATE), PATH_DATE_FORMATTER))
          .endDate(LocalDate.parse(record.get(END_DATE), PATH_DATE_FORMATTER))
          .days(daysOnWhichServiceRuns(record))
          .build());
      }    
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize ServicesResource.", e);
    }
  }

  private EnumSet<DayOfWeek> daysOnWhichServiceRuns(CSVRecord record) {
    EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
    for (DayOfWeek day : DayOfWeek.values()) {
      if (Integer.parseInt(record.get(day.getDisplayName(TextStyle.FULL, Locale.US).toLowerCase())) == 1) {
        days.add(day);
      }
    }
    return days;
  }

  private Predicate<? super Service> isServiceActiveOnDate(LocalDate date) {
    return service -> date.isAfter(service.startDate()) 
        && date.isBefore(service.endDate())
        && service.days().contains(date.getDayOfWeek());
  }
}
