package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class InvalidFilterException extends RuntimeException {

  public InvalidFilterException(String filterType, String category) {
    super(
      "Invalid filter configuration for category: "
        + category
        + ", filter type: "
        + filterType
    );
  }
}
