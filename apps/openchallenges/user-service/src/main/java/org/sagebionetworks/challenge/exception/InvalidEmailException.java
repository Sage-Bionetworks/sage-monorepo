package org.sagebionetworks.challenge.exception;

public class InvalidEmailException extends SimpleChallengeGlobalException {

  public InvalidEmailException(String detail) {
    super(
      ErrorConstants.INVALID_EMAIL.getType(),
      ErrorConstants.INVALID_EMAIL.getTitle(),
      ErrorConstants.INVALID_EMAIL.getStatus(),
      detail
    );
  }
}
