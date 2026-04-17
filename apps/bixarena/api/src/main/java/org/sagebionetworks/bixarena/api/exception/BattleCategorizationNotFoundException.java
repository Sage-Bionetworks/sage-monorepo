package org.sagebionetworks.bixarena.api.exception;

public class BattleCategorizationNotFoundException extends RuntimeException {

  public BattleCategorizationNotFoundException(String message) {
    super(message);
  }

  public BattleCategorizationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
