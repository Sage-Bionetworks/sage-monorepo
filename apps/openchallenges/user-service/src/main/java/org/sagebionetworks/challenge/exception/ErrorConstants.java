package org.sagebionetworks.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  ENTITY_NOT_FOUND("CHALLENGE-USER-SERVICE-1000", "Entity not found", HttpStatus.NOT_FOUND),
  USERNAME_ALREADY_EXISTS(
    "CHALLENGE-USER-SERVICE-1001",
    "Username already exists",
    HttpStatus.CONFLICT
  ),
  INVALID_EMAIL("CHALLENGE-USER-SERVICE-1002", "Invalid email", HttpStatus.BAD_REQUEST),
  INVALID_USER("CHALLENGE-USER-SERVICE-1003", "Invalid user", HttpStatus.BAD_REQUEST);

  private String type;
  private String title;
  private HttpStatus status;
}
