package org.sagebionetworks.challenge.exception;

public class UserAlreadyRegisteredException extends SimpleChallengeGlobalException {
  public UserAlreadyRegisteredException(String message, String code) {
    super(message, code);
  }
}
