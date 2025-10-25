package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

public class DuplicateEvaluationException extends RuntimeException {

  public DuplicateEvaluationException(UUID battleId) {
    super(String.format("An evaluation already exists for battle ID: %s", battleId));
  }
}
