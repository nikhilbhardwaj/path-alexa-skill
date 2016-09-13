package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_STATIONS;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;

import in.nikhilbhardwaj.path.route.model.StopNames;

/**
 * The StopsResource fronts a bi-directional map constructed from the stops.txt Each platform has a
 * different stop_id, this makes it hard to use. The structure of each entry in this map is like
 * stationName -> Set(id1, id2, id3)
 * 
 * This exposes convenience methods to convert between station names and station ids to assist with
 * the route computation.
 */
@Singleton
public final class StopsResource {
  private final BiMap<StopNames, Set<String>> stops;
  private TransitDataRepository transitData;

  /**
   * Returns the stopName that corresponds to the stationId that is passed in.
   * 
   * @param stationId
   * @return the stop name if the stationId points to a valid station in the system otherwise an
   *         empty optional is returned
   */
  public Optional<StopNames> getStopName(String stationId) {
    // Create a Map with entries of type String, String id1,id2,id3 -> stationName
    // this is required because a Set doesn't play well as the key of a map.
    Map<String, StopNames> idsToNameMap = stops.inverse().entrySet().stream()
        .collect(Collectors.toMap(entry -> String.join(",", entry.getKey()), entry -> entry.getValue()));

    return idsToNameMap.entrySet().stream()
        .filter(entry -> entry.getKey().contains(stationId))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  @Inject
  public StopsResource(TransitDataRepository transitData) {
    this.transitData = transitData;
    stops = initializeStops();
  }

  private BiMap<StopNames, Set<String>> initializeStops() {
    BiMap<StopNames, Set<String>> stops = HashBiMap.create();
    try (InputStreamReader stopsReader = new InputStreamReader(transitData.forResource(RESOURCE_STATIONS), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
          .parse(stopsReader);
      for (CSVRecord record : records) {
        StopNames stopName = StopNames.fromString(record.get("stop_name"));
        String stopId = record.get("stop_id");
        if (stops.containsKey(stopName)) {
          stops.get(stopName).add(stopId);
        } else {
          Set<String> stopIdSet = Sets.newTreeSet();
          stopIdSet.add(stopId);
          stops.put(stopName, stopIdSet);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Unable to initialize the Stops resource.", e);
    }
    return ImmutableBiMap.copyOf(stops);
  }
}
