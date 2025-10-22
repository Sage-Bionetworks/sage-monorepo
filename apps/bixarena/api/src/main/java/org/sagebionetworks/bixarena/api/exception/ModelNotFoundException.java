package org.sagebionetworks.bixarena.api.exception;

public class ModelNotFoundException extends RuntimeException {

  public ModelNotFoundException(String message) {
    super(message);
  }

  public ModelNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
