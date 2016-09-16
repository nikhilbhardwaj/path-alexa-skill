package in.nikhilbhardwaj.path.alexa.intent;

import javax.inject.Inject;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

import in.nikhilbhardwaj.path.alexa.speech.CardFactory;
import in.nikhilbhardwaj.path.alexa.speech.HumanReadableTextGenerator;

/**
 * Provide help about how to use the skill.
 */
public class AmazonHelpIntentAction implements IntentAction {
  private HumanReadableTextGenerator textGenerator;
  private CardFactory cardFactory;

  @Inject
  protected AmazonHelpIntentAction(HumanReadableTextGenerator textGenerator, CardFactory cardFactory) {
    this.textGenerator = textGenerator;
    this.cardFactory = cardFactory;
  }

  @Override
  public SpeechletResponse perform(Intent intent, Session session) {
    String speechText = textGenerator.helpResponse();
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);

    return SpeechletResponse.newAskResponse(speech, reprompt, cardFactory.helpCard(speechText));
  }
}
