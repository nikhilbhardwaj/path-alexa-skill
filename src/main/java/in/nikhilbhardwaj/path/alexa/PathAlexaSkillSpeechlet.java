package in.nikhilbhardwaj.path.alexa;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

import in.nikhilbhardwaj.path.alexa.intent.IntentHandlerService;

/*
 * An implementation of a Speechlet which provides simple session lifecycle management (launch,
 * start, end) and delegates all intents to another service.
 */
public class PathAlexaSkillSpeechlet implements Speechlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(PathAlexaSkillSpeechlet.class);
  private final IntentHandlerService intentHandlerService;

  @Inject
  protected PathAlexaSkillSpeechlet(IntentHandlerService intentHandlerService) {
    this.intentHandlerService = intentHandlerService;
  }

  /*
   * Invoked at the start of an Alexa session. Does not handle any intents.
   */
  @Override
  public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
    LOGGER.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
  }

  /*
   * Invoked when the user invokes the Skill without providing an intent.
   * Reroute to AMAZON.HelpIntent
   */
  @Override
  public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
    LOGGER.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    Intent helpIntent = Intent.builder().withName("AMAZON.HelpIntent").build();
    return intentHandlerService.handle("AMAZON.HelpIntent", helpIntent, session);
  }

  /*
   * Invoked by any intent requests.
   */
  @Override
  public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
    LOGGER.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

    Intent intent = request.getIntent();
    String intentName = (intent != null) ? intent.getName() : null;

    return intentHandlerService.handle(intentName, intent, session);
  }

  @Override
  public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
    LOGGER.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());    
  }

}
