package org.sagebionetworks.openchallenges.challenge.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.image.service.exception.SimpleChallengeGlobalException;
import org.springframework.http.HttpStatus;

public class SimpleChallengeGlobalExceptionTest {

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnMessage_WhenMessageKeyIsPassed() {
    // Create an instance of SimpleChallengeGlobalException using the constructor with details
    String details = "Something went wrong";
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(details);

    // Verify the exception details
    assertThat(exception.getMessage()).isEqualTo(details);
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnType_WhenTypeKeyIsPassed() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(
      type,
      title,
      status,
      detail
    );

    // Verify the exception details
    assertThat(exception.getType()).isEqualTo(type);
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnTitle_WhenTitleKeyIsPassed() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(
      type,
      title,
      status,
      detail
    );

    // Verify the exception details
    assertThat(exception.getTitle()).isEqualTo(title);
  }

  @Test
  public void SimpleChallengeGlobalException_ShouldReturnStatus_WhenStatusKeyIsPassed() {
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create an instance of SimpleChallengeGlobalException using the all-args constructor
    SimpleChallengeGlobalException exception = new SimpleChallengeGlobalException(
      type,
      title,
      status,
      detail
    );

    // Verify the exception details
    assertThat(exception.getStatus()).isEqualTo(status);
  }
}
