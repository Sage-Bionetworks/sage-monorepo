package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class DataIntegrityException extends RuntimeException {

  public DataIntegrityException(String field, String value) {
    super("Data integrity error - unexpected " + field + " value: " + value);
  }
}
