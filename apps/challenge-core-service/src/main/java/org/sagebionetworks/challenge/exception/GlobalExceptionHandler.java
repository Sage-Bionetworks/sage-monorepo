package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;
import org.sagebionetworks.challenge.util.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SimpleChallengeGlobalException.class)
  protected ResponseEntity handleGlobalException(
      SimpleChallengeGlobalException simpleChallengeGlobalException, Locale locale) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder().code(simpleChallengeGlobalException.getCode())
            .message(simpleChallengeGlobalException.getMessage()).build());
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<String> handleException(Exception e, Locale locale) {
    return ResponseEntity.badRequest().body("Exception occur inside API " + e);
  }

}
