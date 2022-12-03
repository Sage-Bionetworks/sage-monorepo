package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class InvalidEmailException extends SimpleChallengeGlobalException {
  public InvalidEmailException(String message, String code) {
    super(message, code);
  }
}
