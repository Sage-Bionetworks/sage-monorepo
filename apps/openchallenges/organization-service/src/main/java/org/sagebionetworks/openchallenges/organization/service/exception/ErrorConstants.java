package org.sagebionetworks.openchallenges.organization.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  ENTITY_NOT_FOUND("ORGANIZATION-SERVICE-1000", "Entity not found", HttpStatus.NOT_FOUND),
  ORGANIZATION_ALREADY_EXISTS(
    "ORGANIZATION-SERVICE-1001",
    "Organization already exists",
    HttpStatus.CONFLICT
  ),
  INVALID_ORGANIZATION("ORGANIZATION-SERVICE-1002", "Invalid organization", HttpStatus.BAD_REQUEST),
  BAD_REQUEST("ORGANIZATION-SERVICE-1003", "Bad request", HttpStatus.BAD_REQUEST),
  CHALLENGE_PARTICIPATION_ALREADY_EXISTS(
    "ORGANIZATION-SERVICE-1003",
    "Challenge participation already exists",
    HttpStatus.CONFLICT
  );

  private String type;
  private String title;
  private HttpStatus status;
}
