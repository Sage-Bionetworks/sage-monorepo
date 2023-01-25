package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.exception;

public class KaggleUnauthorizedRequestException extends RuntimeException {

  public KaggleUnauthorizedRequestException() {
    super();
  }

  public KaggleUnauthorizedRequestException(String message) {
    super(message);
  }

  public KaggleUnauthorizedRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
