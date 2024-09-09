package org.sagebionetworks.openchallenges.organization.service.exception;

import java.util.Locale;
import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SimpleChallengeGlobalException.class)
  protected ResponseEntity<BasicErrorDto> handleGlobalException(
    SimpleChallengeGlobalException simpleChallengeGlobalException,
    Locale locale
  ) {
    return new ResponseEntity<>(
      BasicErrorDto.builder()
        .type(simpleChallengeGlobalException.getType())
        .title(simpleChallengeGlobalException.getTitle())
        .status(simpleChallengeGlobalException.getStatus().value())
        .detail(simpleChallengeGlobalException.getDetail())
        .build(),
      simpleChallengeGlobalException.getStatus()
    );
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<BasicErrorDto> handleException(Exception e, Locale locale) {
    return ResponseEntity.internalServerError()
      .body(
        BasicErrorDto.builder()
          .title("An exception occured")
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .build()
      );
  }
}
