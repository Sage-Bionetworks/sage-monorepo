package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class ComparisonToolConfigNotFoundException extends RuntimeException {

  public ComparisonToolConfigNotFoundException(String page) {
    super("Comparison Tool config not found for page: " + page);
  }
}
