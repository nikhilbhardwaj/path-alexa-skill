package in.nikhilbhardwaj.path.alexa;

import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.SLOT_DESTINATION;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.SLOT_ORIGIN;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.google.common.collect.ImmutableMap;

public class PathAlexaSkillSpeechletTest extends GuicyAbstractTest {

  @Test
  public void tripsAreReturnedForValidRequestWithConnectedStations() throws SpeechletException {
    PathAlexaSkillSpeechlet alexaStarterSpeechlet = GUICE.getInstance(PathAlexaSkillSpeechlet.class);
    Map<String, Slot> slots = ImmutableMap.of(
      SLOT_ORIGIN, Slot.builder().withValue("Grove Street").withName(SLOT_ORIGIN).build(),
      SLOT_DESTINATION, Slot.builder().withValue("World Trade Center").withName(SLOT_DESTINATION).build());
    IntentRequest request =
        IntentRequest.builder()
            .withIntent(Intent.builder().withName("PathTrackerIntent")
                .withSlots(slots)
                .build())
            .withRequestId("requestId")
            .build();

    SpeechletResponse speechletResponse = alexaStarterSpeechlet.onIntent(request, buildTestSession());
    String text = ((PlainTextOutputSpeech) speechletResponse.getOutputSpeech()).getText();
    Assert.assertTrue(text.contains("The next train from"));
  }
  
  @Test
  // To complete this route, people need to make a connection.
  // Connections aren't implemented yet.
  public void tripsAreReturnedForValidRequestWithDisconnectedStations() throws SpeechletException {
    PathAlexaSkillSpeechlet alexaStarterSpeechlet = GUICE.getInstance(PathAlexaSkillSpeechlet.class);
    Map<String, Slot> slots = ImmutableMap.of(
      SLOT_ORIGIN, Slot.builder().withValue("Newark").withName(SLOT_ORIGIN).build(),
      SLOT_DESTINATION, Slot.builder().withValue("Hoboken").withName(SLOT_DESTINATION).build());
    IntentRequest request =
        IntentRequest.builder()
            .withIntent(Intent.builder().withName("PathTrackerIntent")
                .withSlots(slots)
                .build())
            .withRequestId("requestId")
            .build();

    SpeechletResponse speechletResponse = alexaStarterSpeechlet.onIntent(request, buildTestSession());
    String text = ((PlainTextOutputSpeech) speechletResponse.getOutputSpeech()).getText();
    Assert.assertTrue(text.contains("Sorry, I couldn't find any trips for you"));
  }

  @Test
  public void repromptIssuedForRequestsMissingDestination() throws SpeechletException {
    PathAlexaSkillSpeechlet alexaStarterSpeechlet = GUICE.getInstance(PathAlexaSkillSpeechlet.class);
    Map<String, Slot> slots = ImmutableMap.of(SLOT_ORIGIN, Slot.builder().withValue("Grove Street").withName(SLOT_ORIGIN).build());
    IntentRequest request =
        IntentRequest.builder()
            .withIntent(Intent.builder().withName("PathTrackerIntent")
                .withSlots(slots)
                .build())
            .withRequestId("requestId")
            .build();

    SpeechletResponse speechletResponse = alexaStarterSpeechlet.onIntent(request, buildTestSession());
    String text = ((PlainTextOutputSpeech) speechletResponse.getOutputSpeech()).getText();
    Assert.assertTrue(text.contains("Try again"));
  }
  
  @Test
  public void repromptIssuedForRequestsWithoutSlots() throws SpeechletException {
    PathAlexaSkillSpeechlet alexaStarterSpeechlet = GUICE.getInstance(PathAlexaSkillSpeechlet.class);
    IntentRequest request =
        IntentRequest.builder()
            .withIntent(Intent.builder().withName("PathTrackerIntent")
                .withSlots(ImmutableMap.of())
                .build())
            .withRequestId("requestId")
            .build();

    SpeechletResponse speechletResponse = alexaStarterSpeechlet.onIntent(request, buildTestSession());
    String text = ((PlainTextOutputSpeech) speechletResponse.getOutputSpeech()).getText();
    Assert.assertTrue(text.contains("Try again"));
  }

  private Session buildTestSession() {
    Session session = Session.builder()
        .withSessionId("sessionId")
        .withUser(User.builder().withUserId("Popo").build())
        .build();
    return session;
  }
}
