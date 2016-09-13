package in.nikhilbhardwaj.path.route.resources;

import java.io.InputStream;

/**
 * This interface abstracts the data source from which the transit data is loaded.
 * Typically this would be S3 or another service accessible and the Filesystem for tests.
 * (This was necessary as AWS Lambda doesn't allow access to classpath resources
 * See <a href="https://stackoverflow.com/questions/39380810/how-to-read-files-from-the-classpath-in-aws-lambda-java">Stackoverflow Question</a>
 * and See <a href="https://forums.aws.amazon.com/thread.jspa?threadID=238933">AWS Forums</a> for answers or lack thereof. )
 */
public interface TransitDataRepository {
  
  /**
   * Given a resource, returns an InputStream that points to the actual transit data.
   * @param resourceName
   * @return
   * It it the Caller's responsibility to close the InputStream returned from here, this should 
   * almost always be used in a try-with-resources block.
   */
  public InputStream forResource(String resourceName);

}
