import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SimpleChallengeGlobalExceptionTest {

  @Test
  void testConstructorWithDetails() {
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
  void Simpl_ShouldReturnSpecifiedArgs() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(type, title, status, detail);

    // Verify the exception details
    assertNull(exception.getMessage());
    assertEquals(type, exception.getType());
    assertEquals(title, exception.getTitle());
    assertEquals(status, exception.getStatus());
    assertEquals(detail, exception.getDetail());
  }
}