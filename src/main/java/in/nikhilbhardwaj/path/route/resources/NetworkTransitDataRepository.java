package in.nikhilbhardwaj.path.route.resources;

import java.io.InputStream;

import javax.inject.Singleton;

import com.amazonaws.services.s3.AmazonS3Client;

@Singleton
public final class NetworkTransitDataRepository implements TransitDataRepository {
  
  private static final AmazonS3Client S3 = new AmazonS3Client();
  private static final String BUCKET = "path-nj-us";

  @Override
  public InputStream forResource(String resourceName) {
    return S3.getObject(BUCKET, resourceName).getObjectContent();
  }

}
