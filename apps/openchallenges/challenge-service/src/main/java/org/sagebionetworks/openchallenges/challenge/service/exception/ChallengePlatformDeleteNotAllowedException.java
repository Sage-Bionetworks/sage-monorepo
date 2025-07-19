package org.sagebionetworks.openchallenges.challenge.service.exception;

public class ChallengePlatformDeleteNotAllowedException extends SimpleChallengeGlobalException {

  public ChallengePlatformDeleteNotAllowedException(String detail) {
    super(
      ErrorConstants.DELETE_NOT_ALLOWED.getType(),
      ErrorConstants.DELETE_NOT_ALLOWED.getTitle(),
      ErrorConstants.DELETE_NOT_ALLOWED.getStatus(),
      detail
    );
  }
}
