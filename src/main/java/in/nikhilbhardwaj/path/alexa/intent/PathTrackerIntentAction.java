package in.nikhilbhardwaj.path.alexa.intent;

import static in.nikhilbhardwaj.path.route.model.Constants.PATH_TIME_ZONE;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.SLOT_DESTINATION;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.SLOT_ORIGIN;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

import in.nikhilbhardwaj.path.alexa.speech.CardFactory;
import in.nikhilbhardwaj.path.alexa.speech.HumanReadableTextGenerator;
import in.nikhilbhardwaj.path.route.model.ImmutableRouteRequest;
import in.nikhilbhardwaj.path.route.model.RouteRequest;
import in.nikhilbhardwaj.path.route.model.StopNames;
import in.nikhilbhardwaj.path.route.model.Trip;
import in.nikhilbhardwaj.path.route.resources.ServicesResource;
import in.nikhilbhardwaj.path.route.resources.TripsResource;

/**
 * This is the heart of this alexa skill, this is where the request is routed to and processed
 * For valid requests, the trips are computed and translated into text that humans can understand
 * and for invalid requests a reprompt is issued.
 */
public class PathTrackerIntentAction implements IntentAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(PathTrackerIntentAction.class);
  
  private ServicesResource services;
  private TripsResource trips;
  private HumanReadableTextGenerator textGenerator;
  private CardFactory cardFactory;
  
  @Inject
  protected PathTrackerIntentAction(ServicesResource services, TripsResource trips,
                                    HumanReadableTextGenerator textGenerator, CardFactory cardFactory) {
    this.services = services;
    this.trips = trips;
    this.textGenerator = textGenerator;
    this.cardFactory = cardFactory;
  }

  @Override
  public SpeechletResponse perform(final Intent intent, final Session session) {
    LOGGER.info("Attempting to perform action for PathTrackerIntent {} and session {}."
      , reflectionToString(intent, new RecursiveToStringStyle()), reflectionToString(session, new RecursiveToStringStyle()));
    try {
      // Both the origin and destination need to be present for us to be able to find trips
      // if that isn't the case then we prompt the user to try again.
      StopNames origin = StopNames.fromString(intent.getSlot(SLOT_ORIGIN).getValue());
      StopNames destination = StopNames.fromString(intent.getSlot(SLOT_DESTINATION).getValue());
      return getTrainScheduleResponse(origin, destination);
    } catch (Exception e) {
      LOGGER.info("Couldn't perform action for intent {} and session {}."
        , reflectionToString(intent, new RecursiveToStringStyle()), reflectionToString(session, new RecursiveToStringStyle()), e);
      return getRepromptResposne();
    }
  }

  private SpeechletResponse getRepromptResposne() {
    String responseText = textGenerator.repromptResponse();
    PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
    plainTextOutputSpeech.setText(responseText);
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(plainTextOutputSpeech);

    return SpeechletResponse.newAskResponse(plainTextOutputSpeech, reprompt, cardFactory.repromptCard(responseText));
  }

  private SpeechletResponse getTrainScheduleResponse(StopNames origin, StopNames destination) {
    RouteRequest request = constructRouteRequestFromIntent(origin, destination);
    // Compute trips for route
    Set<String> servicesRunningToday = services.getServiceIdsForDate(request.date());
    List<Trip> tripsBetweenOriginAndDestination =
        servicesRunningToday.stream()
            .map(service -> trips.getTripsForServiceConnectingOriginAndDestination(service, request))
            .flatMap(allTrips -> allTrips.stream())
            .collect(Collectors.toList());
    LOGGER.info("Found {} for request between {} and {}", tripsBetweenOriginAndDestination.stream().limit(2), origin, destination);
    String responseText = textGenerator.from(tripsBetweenOriginAndDestination); 
    PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
    plainTextOutputSpeech.setText(responseText);

    return SpeechletResponse.newTellResponse(plainTextOutputSpeech, cardFactory.tripsResponseCard(responseText));
  }
  
  private RouteRequest constructRouteRequestFromIntent(StopNames origin, StopNames destination) {
    return ImmutableRouteRequest.builder()
        .date(LocalDate.now(PATH_TIME_ZONE)) // We need to work with EST/EDT because that's 
        .time(LocalTime.now(PATH_TIME_ZONE)) // where PATH runs
        .origin(origin)
        .destination(destination)
        .build();
  }

}
