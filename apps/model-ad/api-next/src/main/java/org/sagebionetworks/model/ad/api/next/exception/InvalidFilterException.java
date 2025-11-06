package org.sagebionetworks.model.ad.api.next.exception;

public class InvalidFilterException extends RuntimeException {

  public InvalidFilterException(String message) {
    super(message);
  }

  public InvalidFilterException(String filterType, String category) {
    super(
      "Invalid filter configuration for category: "
        + category
        + ", filter type: "
        + filterType
    );
  }
}
