package org.sagebionetworks.bixarena.api.exception;

public class BattleValidationNotFoundException extends RuntimeException {

  public BattleValidationNotFoundException(String message) {
    super(message);
  }

  public BattleValidationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
