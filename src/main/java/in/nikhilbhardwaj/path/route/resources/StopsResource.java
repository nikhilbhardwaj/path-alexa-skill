package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCES_ROOT;
import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCE_STATIONS;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class StopsResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(StopsResource.class);
  private final BiMap<StopNames, Set<String>> stops;

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

  public StopsResource() {
    stops = initializeStops();
  }

  private BiMap<StopNames, Set<String>> initializeStops() {
    BiMap<StopNames, Set<String>> stops = HashBiMap.create();
    try {
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
          .parse(new FileReader(RESOURCES_ROOT + RESOURCE_STATIONS));
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
      LOGGER.error("Unable to parse the station info from {}.", RESOURCE_STATIONS, e);
      throw new IllegalStateException("Unable to read from file system.", e);
    }
    return ImmutableBiMap.copyOf(stops);
  }
}
