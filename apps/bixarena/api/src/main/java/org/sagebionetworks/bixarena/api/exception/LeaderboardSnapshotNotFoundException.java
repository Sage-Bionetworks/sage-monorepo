package org.sagebionetworks.bixarena.api.exception;

public class LeaderboardSnapshotNotFoundException extends RuntimeException {

  public LeaderboardSnapshotNotFoundException(String message) {
    super(message);
  }

  public LeaderboardSnapshotNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
