package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a duplicate battle evaluation for the same battle.
 */
public class DuplicateBattleEvaluationException extends RuntimeException {

  public DuplicateBattleEvaluationException(UUID battleId) {
    super(String.format("A battle evaluation already exists for battle ID: %s", battleId));
  }

  public DuplicateBattleEvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
}
