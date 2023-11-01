// import org.junit.Assert;
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.sagebionetworks.openchallenges.image.service.exception.SimpleChallengeGlobalException;
import org.springframework.http.HttpStatus;

public class SimpleChallengeGlobalExceptionTest {

  @Test
  public void testConstructorWithDetails() {
    // Create an instance of SimpleChallengeGlobalException using the constructor with details
    String details = "Something went wrong";
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(details);

    // Verify the exception details
    assertEquals(details, exception.getMessage());
    assertNull(exception.getType());
    assertNull(exception.getTitle());
    assertNull(exception.getStatus());
    assertNull(exception.getDetail());
  }

  @Test
  public void SimpleChallenge_ShouldReturnSpecifiedArgs() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception =
        new SimpleChallengeGlobalException(type, title, status, detail);

    // Verify the exception details
    assertNull(exception.getMessage());
    assertEquals(type, exception.getType());
    assertEquals(title, exception.getTitle());
    assertEquals(status, exception.getStatus());
    assertEquals(detail, exception.getDetail());
  }
}
