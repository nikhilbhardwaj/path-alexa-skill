package in.nikhilbhardwaj.path.alexa;

import com.google.inject.AbstractModule;

import in.nikhilbhardwaj.path.alexa.intent.IntentModule;
import in.nikhilbhardwaj.path.route.resources.NetworkTransitDataRepository;
import in.nikhilbhardwaj.path.route.resources.TransitDataRepository;

/**
 * This is the parent module, it install all the needed modules that can then be
 * used throughout the app.
 */
public class AlexaStarterApplicationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(TransitDataRepository.class).to(NetworkTransitDataRepository.class);
    install(new IntentModule());
  }

}
