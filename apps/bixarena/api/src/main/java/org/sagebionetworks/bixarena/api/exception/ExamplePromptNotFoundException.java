package org.sagebionetworks.bixarena.api.exception;

public class ExamplePromptNotFoundException extends RuntimeException {

  public ExamplePromptNotFoundException(String message) {
    super(message);
  }

  public ExamplePromptNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
