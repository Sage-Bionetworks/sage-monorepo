package org.sagebionetworks.challenge.exception;

public class InvalidUserException extends SimpleChallengeGlobalException {

  public InvalidUserException(String detail) {
    super(
      ErrorConstants.INVALID_USER.getType(),
      ErrorConstants.INVALID_USER.getTitle(),
      ErrorConstants.INVALID_USER.getStatus(),
      detail
    );
  }
}
