package org.sagebionetworks.openchallenges.challenge.service.exception;

public class ChallengeParticipationDeleteException extends SimpleChallengeGlobalException {

  public ChallengeParticipationDeleteException(String detail) {
    super(
      ErrorConstants.CHALLENGE_PARTICIPATION_DELETE_ERROR.getType(),
      ErrorConstants.CHALLENGE_PARTICIPATION_DELETE_ERROR.getTitle(),
      ErrorConstants.CHALLENGE_PARTICIPATION_DELETE_ERROR.getStatus(),
      detail
    );
  }
}
