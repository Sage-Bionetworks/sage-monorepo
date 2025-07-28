package org.sagebionetworks.openchallenges.organization.service.exception;

public class ChallengeParticipationAlreadyExistsException extends SimpleChallengeGlobalException {

  public ChallengeParticipationAlreadyExistsException(String detail) {
    super(
      ErrorConstants.CHALLENGE_PARTICIPATION_ALREADY_EXISTS.getType(),
      ErrorConstants.CHALLENGE_PARTICIPATION_ALREADY_EXISTS.getTitle(),
      ErrorConstants.CHALLENGE_PARTICIPATION_ALREADY_EXISTS.getStatus(),
      detail
    );
  }
}
