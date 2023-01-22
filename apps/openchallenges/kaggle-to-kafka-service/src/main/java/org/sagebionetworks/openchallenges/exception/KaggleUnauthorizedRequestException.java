package org.sagebionetworks.openchallenges.exception;

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
