package org.sagebionetworks.challenge.util.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
  private String code;
  private String message;
}
