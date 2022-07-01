package org.sagebionetworks.challenge.exception;

public class InvalidEmailException extends SimpleChallengeGlobalException {
  public InvalidEmailException(String message, String code) {
    super(message, code);
  }
}
