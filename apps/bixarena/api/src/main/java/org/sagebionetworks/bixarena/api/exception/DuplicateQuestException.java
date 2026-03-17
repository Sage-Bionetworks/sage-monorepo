package org.sagebionetworks.bixarena.api.exception;

public class DuplicateQuestException extends RuntimeException {

  public DuplicateQuestException(String questId) {
    super(String.format("A quest with ID '%s' already exists", questId));
  }
}
