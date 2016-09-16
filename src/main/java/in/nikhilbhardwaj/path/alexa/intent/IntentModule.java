package in.nikhilbhardwaj.path.alexa.intent;

import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.INTENT_CANCEL;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.INTENT_HELP;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.INTENT_PATH_TRACKER;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.INTENT_START_OVER;
import static in.nikhilbhardwaj.path.route.model.Constants.AlexaSkill.INTENT_STOP;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class IntentModule extends AbstractModule {

  /*
   * Configures all IntentAction implementations. Binding them to a Map so that they can be looked
   * up by their Amazon Intent name.
   */
  @Override
  protected void configure() {
    bind(IntentHandlerService.class).to(IntentHandlerServiceImpl.class);

    MapBinder<String, IntentAction> mapBinder =
        MapBinder.newMapBinder(binder(), String.class, IntentAction.class);

    // Amazon intents
    mapBinder.addBinding(INTENT_HELP).to(AmazonHelpIntentAction.class);
    mapBinder.addBinding(INTENT_START_OVER).to(AmazonHelpIntentAction.class);
    mapBinder.addBinding(INTENT_CANCEL).to(AmazonStopIntentAction.class);
    mapBinder.addBinding(INTENT_STOP).to(AmazonStopIntentAction.class);

    // custom intents
    mapBinder.addBinding(INTENT_PATH_TRACKER).to(PathTrackerIntentAction.class);
  }
}
