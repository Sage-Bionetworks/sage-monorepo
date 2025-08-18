package org.sagebionetworks.bixarena.api.exception;

public class LeaderboardNotFoundException extends RuntimeException {

  public LeaderboardNotFoundException(String message) {
    super(message);
  }

  public LeaderboardNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
