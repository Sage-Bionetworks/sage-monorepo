package org.sagebionetworks.openchallenges.challenge.service.exception;

import java.util.Locale;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<BasicErrorDto> handleAccessDeniedException(
    AccessDeniedException e,
    Locale locale
  ) {
    return new ResponseEntity<>(
      BasicErrorDto.builder()
        .title("Access Denied")
        .status(HttpStatus.FORBIDDEN.value())
        .detail("You do not have permission to access this resource.")
        .build(),
      HttpStatus.FORBIDDEN
    );
  }

  @ExceptionHandler(DuplicateContributionException.class)
  protected ResponseEntity<BasicErrorDto> handleDuplicateContributionException(
    DuplicateContributionException e,
    Locale locale
  ) {
    return new ResponseEntity<>(
      BasicErrorDto.builder()
        .title("Duplicate Contribution")
        .status(HttpStatus.CONFLICT.value())
        .detail(e.getMessage())
        .build(),
      HttpStatus.CONFLICT
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
