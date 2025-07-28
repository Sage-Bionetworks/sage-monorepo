package org.sagebionetworks.openchallenges.challenge.service.exception;

public class ChallengeParticipationNotFoundException extends SimpleChallengeGlobalException {

  public ChallengeParticipationNotFoundException(String detail) {
    super(
      ErrorConstants.CHALLENGE_PARTICIPATION_NOT_FOUND.getType(),
      ErrorConstants.CHALLENGE_PARTICIPATION_NOT_FOUND.getTitle(),
      ErrorConstants.CHALLENGE_PARTICIPATION_NOT_FOUND.getStatus(),
      detail
    );
  }
}
