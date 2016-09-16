package in.nikhilbhardwaj.path.alexa.intent;

import javax.inject.Inject;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

import in.nikhilbhardwaj.path.alexa.speech.HumanReadableTextGenerator;

/**
 * An intent that supports either of the following actions: - Let the user stop an action (but
 * remain in the skill) - Let the user completely exit the skill
 */
public class AmazonStopIntentAction implements IntentAction {

  private final HumanReadableTextGenerator textGenerator;

  @Inject
  protected AmazonStopIntentAction(HumanReadableTextGenerator textGenerator) {
    this.textGenerator = textGenerator;
  }

  @Override
  public SpeechletResponse perform(Intent intent, Session session) {
    String speechText = textGenerator.sessionEndResponse();
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    return SpeechletResponse.newTellResponse(speech);
  }

}
