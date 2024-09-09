package org.sagebionetworks.openchallenges.challenge.service.exception;

public class InvalidChallengeException extends SimpleChallengeGlobalException {

  public InvalidChallengeException(String detail) {
    super(
      ErrorConstants.ENTITY_NOT_FOUND.getType(),
      ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
      ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
      detail
    );
  }
}
