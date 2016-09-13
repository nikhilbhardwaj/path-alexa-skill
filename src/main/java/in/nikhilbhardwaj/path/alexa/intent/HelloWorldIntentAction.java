package in.nikhilbhardwaj.path.alexa.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.StandardCard;

import in.nikhilbhardwaj.path.route.model.ImmutableRouteRequest;
import in.nikhilbhardwaj.path.route.model.RouteRequest;
import in.nikhilbhardwaj.path.route.model.StopNames;
import in.nikhilbhardwaj.path.route.model.Trip;
import in.nikhilbhardwaj.path.route.resources.ServicesResource;
import in.nikhilbhardwaj.path.route.resources.StopTimesResource;
import in.nikhilbhardwaj.path.route.resources.StopsResource;
import in.nikhilbhardwaj.path.route.resources.TripsResource;

import static in.nikhilbhardwaj.path.route.model.StopNames.NEWPORT;
import static in.nikhilbhardwaj.path.route.model.StopNames.TWENTY_THIRD_ST;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class HelloWorldIntentAction implements IntentAction {

  private static final String SLOT_NAME = "Name";
  private ServicesResource services;
  private StopsResource stops;
  private StopTimesResource stopTimes;
  private TripsResource trips;

  protected HelloWorldIntentAction() {}
  
  @Inject
  protected HelloWorldIntentAction(ServicesResource services, StopsResource stops
                                   , StopTimesResource stopTimes, TripsResource trips) {
    this.services = services;
    this.stops = stops;
    this.stopTimes = stopTimes;
    this.trips = trips;
  }

  @Override
  public SpeechletResponse perform(final Intent intent, final Session session) {
    return Optional.ofNullable(intent.getSlot(SLOT_NAME)).map(Slot::getValue)
        .map(name -> getHelloResponse(name)).orElse(getRepromptResposne());
  }

  /*
   * Returns a SpeechletResponse which reprompts the user to try again.
   */
  private SpeechletResponse getRepromptResposne() {
    
    RouteRequest request = ImmutableRouteRequest.builder()
        .date(LocalDate.of(2016, 9, 6))
        .time(LocalTime.of(9, 30))
        .origin(NEWPORT)
        .destination(TWENTY_THIRD_ST)
        .build();

    Set<String> servicesRunningToday = services.getServiceIdsForDate(request.date());
    List<Trip> tripsBetweenOriginAndDestination =
        servicesRunningToday.stream()
            .map(service -> trips.getTripsForServiceConnectingOriginAndDestination(service, request))
            .flatMap(allTrips -> allTrips.stream())
            .collect(Collectors.toList());

    //final String responseText = "I am sorry, who are you? Try saying 'hello, Alexa'";
    String responseText = tripsBetweenOriginAndDestination.toString();
    StandardCard standardCard = new StandardCard();
    standardCard.setTitle("Who are you?");
    standardCard.setText(responseText);

    PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
    plainTextOutputSpeech.setText(responseText);

    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(plainTextOutputSpeech);

    return SpeechletResponse.newAskResponse(plainTextOutputSpeech, reprompt, standardCard);
  }

  /*
   * Returns a SpeechletResponse which says hello.
   */
  private SpeechletResponse getHelloResponse(final String name) {
    StandardCard standardCard = new StandardCard();

    final String responseText = "Hello, " + name;
    //String responseText = tripsBetweenOriginAndDestination.toString();

    standardCard.setTitle("Hello, World");
    standardCard.setText(responseText);

    PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
    plainTextOutputSpeech.setText(responseText);

    return SpeechletResponse.newTellResponse(plainTextOutputSpeech, standardCard);
  }

}
