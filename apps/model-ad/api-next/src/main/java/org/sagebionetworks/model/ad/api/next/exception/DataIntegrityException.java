package org.sagebionetworks.model.ad.api.next.exception;

public class DataIntegrityException extends RuntimeException {

  public DataIntegrityException(String message) {
    super(message);
  }

  public DataIntegrityException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataIntegrityException(String field, String value) {
    super("Data integrity error - unexpected " + field + " value: " + value);
  }
}
