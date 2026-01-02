package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class ModelNotFoundException extends RuntimeException {

  public ModelNotFoundException(String name) {
    super("Model not found: " + name);
  }
}
