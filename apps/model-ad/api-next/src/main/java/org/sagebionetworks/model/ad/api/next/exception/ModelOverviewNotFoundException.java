package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class ModelOverviewNotFoundException extends RuntimeException {

  public ModelOverviewNotFoundException(String name) {
    super("Model overview not found with name: " + name);
  }
}
