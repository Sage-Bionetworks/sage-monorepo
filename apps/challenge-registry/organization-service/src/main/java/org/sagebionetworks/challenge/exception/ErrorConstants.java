package org.sagebionetworks.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  ENTITY_NOT_FOUND("CHALLENGE-ORGANIZATION-SERVICE-1000", "Entity not found", HttpStatus.NOT_FOUND),
  ORGANIZATION_ALREADY_EXISTS(
      "CHALLENGE-ORGANIZATION-SERVICE-1001", "Organization already exists", HttpStatus.CONFLICT),
  INVALID_ORGANIZATION(
      "CHALLENGE-ORGANIZATION-SERVICE-1002", "Invalid organization", HttpStatus.BAD_REQUEST);

  private String type;
  private String title;
  private HttpStatus status;
}
