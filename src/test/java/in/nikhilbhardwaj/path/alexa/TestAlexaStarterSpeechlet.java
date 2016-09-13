package in.nikhilbhardwaj.path.alexa;

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

public class TestAlexaStarterSpeechlet extends GuicyAbstractTest {

  @Test
  public void sampleTest() throws SpeechletException {
    // create the speechlet
    AlexaStarterSpeechlet alexaStarterSpeechlet = GUICE.getInstance(AlexaStarterSpeechlet.class);

    // build a sample IntentRequest
    IntentRequest request =
        IntentRequest.builder()
            .withIntent(Intent.builder().withName("HelloWorldIntent")
                .withSlots(ImmutableMap.of("Name",
                    Slot.builder().withValue("Alexa").withName("Name").build()))
                .build())
            .withRequestId("id").build();

    // build a sample Session
    Session session = Session.builder().withSessionId("id")
        .withUser(User.builder().withUserId("a").build()).build();

    SpeechletResponse speechletResponse = alexaStarterSpeechlet.onIntent(request, session);
    String text = ((PlainTextOutputSpeech) speechletResponse.getOutputSpeech()).getText();
    System.out.println(text);
    //assertTrue(text.equals("Hello, Alexa"));
  }

}
