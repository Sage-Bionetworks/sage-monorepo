package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

  @Test
  public void
      GlobalExceptionHandler_ShouldReturnInternalServerErrorStatusCode_WhenExceptionAndLocalePassed() {

    // Create a sample Exception
    Exception exception = new Exception("An exception occurred");
    Locale locale = Locale.getDefault();

    // Create the GlobalExceptionHandler instance
    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    // Call handleException
    ResponseEntity<BasicErrorDto> responseEntity =
        exceptionHandler.handleException(exception, locale);

    // Verify the Response Entity matches the Internal Server Error Code
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void
      GlobalExceptionHandler_ShouldReturnStatusCodeOfSimpleChallengeGlobalExceptionResponseEntityObject_WhenArgsPassedToSimpleChallengeGlobalException() {

    // Create a sample Exception
    Locale locale = Locale.getDefault();

    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    // Create newSimpleChallengeGlobalException instance
    SimpleChallengeGlobalException exception =
        new SimpleChallengeGlobalException(type, title, status, detail);

    // Create the GlobalExceptionHandler instance
    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    // Call handleGlobalException
    ResponseEntity<BasicErrorDto> responseEntity =
        exceptionHandler.handleGlobalException(exception, locale);

    // confirm that the status code that was set was retrieved and applied to the Response Entity
    // object
    assertThat(responseEntity.getStatusCode()).isEqualTo(exception.getStatus());
  }
}
