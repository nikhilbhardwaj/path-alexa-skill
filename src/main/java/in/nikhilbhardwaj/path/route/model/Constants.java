package in.nikhilbhardwaj.path.route.model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
  public static final DateTimeFormatter PATH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  public static final ZoneId PATH_TIME_ZONE = ZoneId.of("America/New_York");

  public static class Filenames {
    public static final String RESOURCE_CALENDAR = "calendar.txt";
    public static final String RESOURCE_CALENDAR_DATES = "calendar_dates.txt";
    public static final String RESOURCES_ROOT = "/path-nj-us/";
    public static final String RESOURCE_STATIONS = "stops.txt";
    public static final String RESOURCE_STOP_TIMES = "stop_times.txt";
    public static final String RESOURCE_TRIPS = "trips.txt";
  }

  public static class CsvHeaders {
    public static final String DATE = "date";
    public static final String DEPARTURE_TIME = "departure_time";
    public static final String END_DATE = "end_date";
    public static final String ROUTE_ID = "route_id";
    public static final String SERVICE_ID = "service_id";
    public static final String START_DATE = "start_date";
    public static final String STOP_ID = "stop_id";
    public static final String TRIP_ID = "trip_id";
  }
}
