package org.sagebionetworks.challenge.exception;

import java.util.Locale;
import org.sagebionetworks.challenge.model.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SimpleChallengeGlobalException.class)
  protected ResponseEntity<ErrorDto> handleGlobalException(
      SimpleChallengeGlobalException simpleChallengeGlobalException, Locale locale) {
    return new ResponseEntity<>(
        ErrorDto.builder()
            .type(simpleChallengeGlobalException.getType())
            .title(simpleChallengeGlobalException.getTitle())
            .status(simpleChallengeGlobalException.getStatus().value())
            .detail(simpleChallengeGlobalException.getDetail())
            .build(),
        simpleChallengeGlobalException.getStatus());
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<String> handleException(Exception e, Locale locale) {
    return ResponseEntity.badRequest().body("Exception occur inside API " + e);
  }
}
