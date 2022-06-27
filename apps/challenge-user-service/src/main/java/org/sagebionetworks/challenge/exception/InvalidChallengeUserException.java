package org.sagebionetworks.challenge.exception;

public class InvalidChallengeUserException extends SimpleChallengeGlobalException {
  public InvalidChallengeUserException(String message, String code) {
    super(message, code);
  }
}
