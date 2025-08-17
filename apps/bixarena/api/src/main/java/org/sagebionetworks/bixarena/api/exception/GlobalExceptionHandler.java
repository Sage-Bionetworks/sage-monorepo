package org.sagebionetworks.bixarena.api.exception;

import java.util.Locale;
import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(
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
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(
        BasicErrorDto.builder()
          .title("Leaderboard Not Found")
          .status(HttpStatus.NOT_FOUND.value())
          .detail(ex.getMessage())
          .build()
      );
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<BasicErrorDto> handleGenericException(Exception ex, Locale locale) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(
        BasicErrorDto.builder()
          .title("Internal Server Error")
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .detail("An unexpected error occurred")
          .build()
      );
  }
}