package in.nikhilbhardwaj.path.route.resources;

import static in.nikhilbhardwaj.path.route.model.Constants.Filenames.RESOURCES_ROOT;

import java.io.InputStream;

import javax.inject.Singleton;

@Singleton
public final class LocalTransitDataRepository implements TransitDataRepository {

  @Override
  public InputStream forResource(String resourceName) {
    return this.getClass().getResourceAsStream(RESOURCES_ROOT + resourceName);
  }
}
