package org.sagebionetworks.challenge.exception;

import org.sagebionetworks.challenge.util.exception.SimpleChallengeGlobalException;

public class EntityNotFoundException extends SimpleChallengeGlobalException {
  public EntityNotFoundException() {
    super("Requested entity not present in the DB.", GlobalErrorCode.ERROR_ENTITY_NOT_FOUND);
  }

  public EntityNotFoundException(String message) {
    super(message, GlobalErrorCode.ERROR_ENTITY_NOT_FOUND);
  }
}
