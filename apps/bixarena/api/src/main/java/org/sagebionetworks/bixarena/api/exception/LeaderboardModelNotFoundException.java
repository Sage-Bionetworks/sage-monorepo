package org.sagebionetworks.bixarena.api.exception;

public class LeaderboardModelNotFoundException extends RuntimeException {

  public LeaderboardModelNotFoundException(String message) {
    super(message);
  }

  public LeaderboardModelNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
