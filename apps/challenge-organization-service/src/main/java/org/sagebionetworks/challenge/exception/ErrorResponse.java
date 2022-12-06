package org.sagebionetworks.challenge.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorResponse {
  private String type;
  private String title;
  private HttpStatus status;
  private String detail;
}
