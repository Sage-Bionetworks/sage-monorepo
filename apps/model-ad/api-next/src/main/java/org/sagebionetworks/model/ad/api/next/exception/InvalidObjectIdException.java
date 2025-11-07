package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class InvalidObjectIdException extends RuntimeException {

  public InvalidObjectIdException(String value) {
    super("Invalid ObjectId format: " + value);
  }

  public InvalidObjectIdException(String value, Throwable cause) {
    super("Invalid ObjectId format: " + value, cause);
  }
}
