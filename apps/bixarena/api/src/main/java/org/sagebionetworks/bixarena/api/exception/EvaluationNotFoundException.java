package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

public class EvaluationNotFoundException extends RuntimeException {

  public EvaluationNotFoundException(UUID evaluationId) {
    super(String.format("Evaluation not found with ID: %s", evaluationId));
  }
}
