package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class InvalidUserException extends SimpleChallengeGlobalException {
  public InvalidUserException(String message, String code) {
    super(message, code);
  }
}
