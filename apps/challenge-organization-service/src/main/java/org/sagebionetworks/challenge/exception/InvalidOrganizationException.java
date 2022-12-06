package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class InvalidOrganizationException extends SimpleChallengeGlobalException {
  public InvalidOrganizationException(String message, String code) {
    super(message, code);
  }
}
