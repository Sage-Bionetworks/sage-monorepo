package org.sagebionetworks.bixarena.api.exception;

public class BattleNotEligibleForCategorizationException extends RuntimeException {

  public BattleNotEligibleForCategorizationException(String message) {
    super(message);
  }

  public BattleNotEligibleForCategorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}
