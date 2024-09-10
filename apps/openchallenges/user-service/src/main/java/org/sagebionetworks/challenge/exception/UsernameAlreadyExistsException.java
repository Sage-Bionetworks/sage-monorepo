package org.sagebionetworks.challenge.exception;

public class UsernameAlreadyExistsException extends SimpleChallengeGlobalException {

  public UsernameAlreadyExistsException(String detail) {
    super(
      ErrorConstants.USERNAME_ALREADY_EXISTS.getType(),
      ErrorConstants.USERNAME_ALREADY_EXISTS.getTitle(),
      ErrorConstants.USERNAME_ALREADY_EXISTS.getStatus(),
      detail
    );
  }
}
