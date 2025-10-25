package org.sagebionetworks.bixarena.api.exception;

import java.util.Locale;
import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(LeaderboardSnapshotNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleSnapshotNotFound(
    LeaderboardSnapshotNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Leaderboard Snapshot Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(LeaderboardNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleLeaderboardNotFound(
    LeaderboardNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Leaderboard Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(LeaderboardModelNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleModelNotFound(
    LeaderboardModelNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Model Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(BattleNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleBattleNotFound(
    BattleNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Battle Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(EvaluationNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleEvaluationNotFound(
    EvaluationNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Evaluation Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(DuplicateEvaluationException.class)
  protected ResponseEntity<BasicErrorDto> handleDuplicateEvaluation(
    DuplicateEvaluationException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      BasicErrorDto.builder()
        .title("Duplicate Evaluation")
        .status(HttpStatus.CONFLICT.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<BasicErrorDto> handleAccessDenied(
    AccessDeniedException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
      BasicErrorDto.builder()
        .title("Forbidden")
        .status(HttpStatus.FORBIDDEN.value())
        .detail("Access denied: " + ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<BasicErrorDto> handleGenericException(Exception ex, Locale locale) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      BasicErrorDto.builder()
        .title("Internal Server Error")
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .detail("An unexpected error occurred")
        .build()
    );
  }
}
