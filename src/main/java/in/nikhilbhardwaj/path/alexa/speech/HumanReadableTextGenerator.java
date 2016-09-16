package in.nikhilbhardwaj.path.alexa.speech;

import java.util.List;

import in.nikhilbhardwaj.path.route.model.Trip;

/**
 * This class is responsible to inspect the trip data from the Route Response and 
 * convert it to something that Alexa can read out to the user.
 */
public class HumanReadableTextGenerator {
  
  /**
   * Returns at most two upcoming trips, rationale behind returning two trips is that
   * the next closest trip may be in the next minute or so; this isn't enough time for the 
   * user to get to the station.
   * @param trips
   * @return
   */
  public String from(List<Trip> trips) {
    StringBuilder tripsResponse = new StringBuilder();
    if (trips.isEmpty()) {
      tripsResponse.append("Sorry, I couldn't find any trips for you. This could be because "
          + "there are no trains between the two stations or this trip needs a connection. "
          + "Connections aren't supported right now, but may be in the future.");
    } else {
      tripsResponse.append("The next train ");
      tripsResponse.append(constructResponseForTrip(trips.get(0)));
      if (trips.size() > 1) {
        tripsResponse.append(" and the one after that is at ");
        tripsResponse.append(trips.get(1).origin().get().time().toString());
      }
    }
    return tripsResponse.toString();
  }
  
  public String repromptResponse() {
    return "I am sorry, I didn't catch that. Try again or ask me for help.";
  }
  
  public String helpResponse() {
    return "Welcome to PathTracker, you can ask me when is the next train from Exchange Place to Newark.";
  }
  
  public String sessionEndResponse() {
    return "Good bye, go catch your train!";
  }
  
  private String constructResponseForTrip(Trip trip) {
    return String.format("from %s to %s is at %s"
      , trip.origin().get().stopName().getName() // Massive law of Demeter violations
      , trip.destination().get().stopName().getName() // Please don't judge me by the code here
      , trip.origin().get().time().toString() //TODO: Convert this to AM/PM times?
      );
  }
}
