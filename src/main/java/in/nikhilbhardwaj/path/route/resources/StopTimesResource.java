package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.DEPARTURE_TIME;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.STOP_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.CsvHeaders.TRIP_ID;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCES_ROOT;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_STOP_TIMES;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import in.nikhilbhardwaj.path.route.model.ImmutableStopTime;
import in.nikhilbhardwaj.path.route.model.StopTime;

/**
 * The StopTimes resource is an abstraction built on top of the stop_times.txt and uses the
 * {@link StopsResource} to find the corresponding stop name from the stopId.
 */
public class StopTimesResource {
  private List<StopTime> stopTimes;
  private StopsResource stopsResource;

  public Set<StopTime> getStopTimesForTrip(String tripId) {
    return stopTimes.stream()
        .filter(stopTime -> stopTime.tripId().equalsIgnoreCase(tripId))
        .collect(Collectors.toSet());
  }

  public StopTimesResource(StopsResource stopsResource) {
    try {
      this.stopsResource = stopsResource;
      initializeStopTimes();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize the StopTimes resource.", e);
    }
  }

  private void initializeStopTimes() throws FileNotFoundException, IOException {
    stopTimes = new ArrayList<>();
    Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
        .parse(new FileReader(RESOURCES_ROOT + RESOURCE_STOP_TIMES));
    for (CSVRecord record : records) {
      stopTimes.add(ImmutableStopTime.builder()
          .stopName(stopsResource.getStopName(record.get(STOP_ID)).get())
          .time(parseLocalTime(record))
          .tripId(record.get(TRIP_ID))
          .build());
    }
  }

  // TODO: Fix later
  // There are instances where the times are like 24:02:00
  // LocalTime only supports hours between 00 and 23
  // We swap this with with values like 00:02:00
  // Unfortunately this will create problems as some trips
  // that originate around midnight will be dropped from our search queries
  // I'm skipping this for now, but this should be fixed at some time in the future.
  private LocalTime parseLocalTime(CSVRecord record) {
    String timeRepresentation = record.get(DEPARTURE_TIME);
    return LocalTime.parse(timeRepresentation.replaceFirst("^24:", "00:"));
  }
}
