package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.exception;

public class KaggleToKafkaServiceException extends RuntimeException {

  public KaggleToKafkaServiceException() {
    super();
  }

  public KaggleToKafkaServiceException(String message) {
    super(message);
  }

  public KaggleToKafkaServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
