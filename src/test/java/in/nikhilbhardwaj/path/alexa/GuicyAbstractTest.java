package in.nikhilbhardwaj.path.alexa;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import in.nikhilbhardwaj.path.alexa.intent.IntentModule;
import in.nikhilbhardwaj.path.route.resources.LocalTransitDataRepository;
import in.nikhilbhardwaj.path.route.resources.TransitDataRepository;

// DI Setup for Unit Tests
public abstract class GuicyAbstractTest {
  protected static final Injector GUICE = Guice.createInjector(new AlexaStarterApplicationUnitTestModule());
  
  private static class AlexaStarterApplicationUnitTestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(TransitDataRepository.class).to(LocalTransitDataRepository.class);
      install(new IntentModule());
    }
  }
}
