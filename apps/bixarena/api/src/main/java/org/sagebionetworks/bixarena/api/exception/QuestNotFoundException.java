package org.sagebionetworks.bixarena.api.exception;

/**
 * Exception thrown when a requested quest is not found.
 */
public class QuestNotFoundException extends RuntimeException {

  public QuestNotFoundException(String message) {
    super(message);
  }

  public QuestNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
