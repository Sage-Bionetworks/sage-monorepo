package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a duplicate battle validation
 * for the same battle, method, and reviewer.
 */
public class DuplicateBattleValidationException extends RuntimeException {

  public DuplicateBattleValidationException(UUID battleId, String method) {
    super(String.format(
      "A validation with method '%s' already exists for battle ID: %s", method, battleId));
  }
}
