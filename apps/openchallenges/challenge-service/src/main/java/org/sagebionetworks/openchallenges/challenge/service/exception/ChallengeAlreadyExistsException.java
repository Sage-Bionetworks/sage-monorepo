package org.sagebionetworks.openchallenges.challenge.service.exception;

public class ChallengeAlreadyExistsException extends SimpleChallengeGlobalException {

  public ChallengeAlreadyExistsException(String detail) {
    super(
      ErrorConstants.ENTITY_NOT_FOUND.getType(),
      ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
      ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
      detail
    );
  }
}
