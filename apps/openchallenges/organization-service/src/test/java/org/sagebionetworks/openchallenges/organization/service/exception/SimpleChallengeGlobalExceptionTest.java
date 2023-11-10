package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SimpleChallengeGlobalExceptionTest {

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnMessage_WhenMessageKeyIsPassed() {
    // Create an instance of SimpleChallengeGlobalException using the constructor with details
    String details = "Something went wrong";
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(details);

    // Verify the exception details
    
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnStatus_WhenStatusKeyIsPassed() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception =
        new SimpleChallengeGlobalException(type, title, status, detail);

    // Verify the exception details
    assertThat(exception.getStatus()).isEqualTo(status);
    assertThat(exception.getTitle()).isEqualTo(title);
    assertThat(exception.getType()).isEqualTo(type);
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldFunction_WhenCalledUsingNoArgsConstructor() {

    // Create an instance of SimpleChallengeGlobalException using the no-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException();
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldSetArgs_WhenArgsPassed() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the no-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException();

    // Create args for the exception
    exception.setType(type);
    exception.setTitle(title);
    exception.setStatus(status);
    exception.setDetail(detail);
  }
}
