package org.sagebionetworks.model.ad.api.next.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a gene expression is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class GeneExpressionNotFoundException extends RuntimeException {

  public GeneExpressionNotFoundException(String message) {
    super(message);
  }

  public GeneExpressionNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
