package org.sagebionetworks.model.ad.api.next.exception;

import java.util.List;

public class InvalidCategoryException extends RuntimeException {

  public InvalidCategoryException(String message) {
    super(message);
  }

  public InvalidCategoryException(List<String> category) {
    super("Invalid category: " + category);
  }

  public InvalidCategoryException(String category, String supportedCategory) {
    super(
      "Category '" + category + "' is not supported. Only '" + supportedCategory + "' is supported"
    );
  }
}
