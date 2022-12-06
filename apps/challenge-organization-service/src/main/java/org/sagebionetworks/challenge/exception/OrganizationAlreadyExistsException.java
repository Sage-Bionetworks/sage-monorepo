package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class OrganizationAlreadyExistsException extends SimpleChallengeGlobalException {
  public OrganizationAlreadyExistsException(String message, String code) {
    super(message, code);
  }
}
