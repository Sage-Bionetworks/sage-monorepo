package org.sagebionetworks.amp.als.dataset.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  ENTITY_NOT_FOUND("DATASET-SERVICE-1000", "Entity not found", HttpStatus.NOT_FOUND),
  BAD_REQUEST("DATASET-SERVICE-1001", "Bad request", HttpStatus.BAD_REQUEST);

  private String type;
  private String title;
  private HttpStatus status;
}
