package org.sagebionetworks.challenge.exception;

import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SimpleChallengeGlobalException.class)
  protected ResponseEntity<ErrorResponse> handleGlobalException(
      SimpleChallengeGlobalException simpleChallengeGlobalException, Locale locale) {
    return new ResponseEntity<ErrorResponse>(
        ErrorResponse.builder()
            .type(simpleChallengeGlobalException.getType())
            .status(simpleChallengeGlobalException.getStatus())
            .title(simpleChallengeGlobalException.getTitle())
            .detail(simpleChallengeGlobalException.getDetail())
            .build(),
        simpleChallengeGlobalException.getStatus());
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<String> handleException(Exception e, Locale locale) {
    return ResponseEntity.badRequest().body("Exception occur inside API " + e);
  }
}
