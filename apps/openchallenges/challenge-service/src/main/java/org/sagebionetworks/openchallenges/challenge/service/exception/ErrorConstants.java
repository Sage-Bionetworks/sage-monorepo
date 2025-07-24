package org.sagebionetworks.openchallenges.challenge.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  ENTITY_NOT_FOUND("CHALLENGE-SERVICE-1000", "Entity not found", HttpStatus.NOT_FOUND),
  ORGANIZATION_ALREADY_EXISTS(
    "CHALLENGE-SERVICE-1001",
    "Challenge already exists",
    HttpStatus.CONFLICT
  ),
  INVALID_ORGANIZATION("CHALLENGE-SERVICE-1002", "Invalid challenge", HttpStatus.BAD_REQUEST),
  BAD_REQUEST("CHALLENGE-SERVICE-1003", "Bad request", HttpStatus.BAD_REQUEST),
  SORT_NOT_IMPLEMENTED(
    "CHALLENGE-SERVICE-1004",
    "Sorting strategy not implemented",
    HttpStatus.NOT_IMPLEMENTED
  ),
  DELETE_NOT_ALLOWED("CHALLENGE-SERVICE-1005", "Delete not allowed", HttpStatus.CONFLICT);

  private String type;
  private String title;
  private HttpStatus status;
}
