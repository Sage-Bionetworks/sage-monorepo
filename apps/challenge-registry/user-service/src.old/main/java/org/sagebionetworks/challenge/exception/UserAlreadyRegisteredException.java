package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class UserAlreadyRegisteredException extends SimpleChallengeGlobalException {
  public UserAlreadyRegisteredException(String message, String code) {
    super(message, code);
  }
}
