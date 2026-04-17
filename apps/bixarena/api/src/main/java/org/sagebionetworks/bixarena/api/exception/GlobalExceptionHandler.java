package org.sagebionetworks.bixarena.api.exception;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
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
  protected ResponseEntity<BasicErrorDto> handleLeaderboardModelNotFoundException(
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

  @ExceptionHandler(ModelNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleModelNotFoundException(
    ModelNotFoundException ex,
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

  @ExceptionHandler(QuestNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleQuestNotFoundException(
    QuestNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Quest Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(BattleValidationNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleBattleValidationNotFound(
    BattleValidationNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Battle Validation Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(ExamplePromptNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleExamplePromptNotFound(
    ExamplePromptNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Example Prompt Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(ExamplePromptCategorizationNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleExamplePromptCategorizationNotFound(
    ExamplePromptCategorizationNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Example Prompt Categorization Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(BattleCategorizationNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleBattleCategorizationNotFound(
    BattleCategorizationNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Battle Categorization Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(BattleNotEligibleForCategorizationException.class)
  protected ResponseEntity<BasicErrorDto> handleBattleNotEligibleForCategorization(
    BattleNotEligibleForCategorizationException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      BasicErrorDto.builder()
        .title("Battle Not Eligible For Categorization")
        .status(HttpStatus.CONFLICT.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(QuestPostNotFoundException.class)
  protected ResponseEntity<BasicErrorDto> handleQuestPostNotFoundException(
    QuestPostNotFoundException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      BasicErrorDto.builder()
        .title("Quest Post Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(DuplicateBattleValidationException.class)
  protected ResponseEntity<BasicErrorDto> handleDuplicateBattleValidation(
    DuplicateBattleValidationException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      BasicErrorDto.builder()
        .title("Duplicate Battle Validation")
        .status(HttpStatus.CONFLICT.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(DuplicateQuestException.class)
  protected ResponseEntity<BasicErrorDto> handleDuplicateQuestException(
    DuplicateQuestException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      BasicErrorDto.builder()
        .title("Duplicate Quest")
        .status(HttpStatus.CONFLICT.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(DuplicateBattleEvaluationException.class)
  protected ResponseEntity<BasicErrorDto> handleDuplicateBattleEvaluation(
    DuplicateBattleEvaluationException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      BasicErrorDto.builder()
        .title("Duplicate Battle Evaluation")
        .status(HttpStatus.CONFLICT.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  protected ResponseEntity<BasicErrorDto> handleConstraintViolationException(
    jakarta.validation.ConstraintViolationException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      BasicErrorDto.builder()
        .title("Bad Request")
        .status(HttpStatus.BAD_REQUEST.value())
        .detail(ex.getMessage())
        .build()
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<BasicErrorDto> handleIllegalArgumentException(
    IllegalArgumentException ex,
    Locale locale
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      BasicErrorDto.builder()
        .title("Bad Request")
        .status(HttpStatus.BAD_REQUEST.value())
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
    log.error("Unhandled exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      BasicErrorDto.builder()
        .title("Internal Server Error")
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .detail("An unexpected error occurred")
        .build()
    );
  }
}
