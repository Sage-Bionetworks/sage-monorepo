package org.sagebionetworks.openchallenges.organization.service.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.beans.Transient;
import java.util.Locale;
import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.organization.service.exception.SimpleChallengeGlobalException;
import org.springframework.http.HttpStatus;

public class GlobalExceptionHandlerTest {
    
  @Test
  public void HandleException_ShouldReturnStatus_WhenExceptionAndLocalePassed() {
    // Create a sample Exception
    Exception exception = new Exception("An exception occurred");
    Locale locale = Locale.getDefault();
    String details = "Something went wrong";

    // Create the GlobalExceptionHandler instance
    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    // Call handleException
    ResponseEntity<BasicErrorDto> responseEntity = exceptionHandler.handleException(exception, locale);

    // Verify the response
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void GlobalExceptionHandler_ShouldFunction_WhenCalled() {
    // Create a sample Exception
    //Exception exception = new Exception("An exception occurred");
    Locale locale = Locale.getDefault();
    // String details = "Something went wrong";
    // Define the exception details
    String type = "ExceptionType";
    String title = "Exception Title";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String detail = "Exception detail message";

    //Create newSimpleChallengeGlobalException instance
    SimpleChallengeGlobalException exception =
        new SimpleChallengeGlobalException(type, title, status, detail);

    // Create the GlobalExceptionHandler instance
    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    //Call handleGlobalException
    ResponseEntity<BasicErrorDto> responseEntity = exceptionHandler.handleGlobalException(exception, locale);

  }
}