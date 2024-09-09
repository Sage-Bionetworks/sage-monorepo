package org.sagebionetworks.openchallenges.challenge.service.exception;

public class ChallengePlatformNotFoundException extends SimpleChallengeGlobalException {

  public ChallengePlatformNotFoundException(String detail) {
    super(
      ErrorConstants.ENTITY_NOT_FOUND.getType(),
      ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
      ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
      detail
    );
  }
}
