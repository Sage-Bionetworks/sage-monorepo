package org.sagebionetworks.bixarena.api.exception;

public class BattleNotFoundException extends RuntimeException {

  public BattleNotFoundException(String message) {
    super(message);
  }

  public BattleNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
