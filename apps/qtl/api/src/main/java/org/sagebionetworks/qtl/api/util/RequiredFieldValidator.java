package org.sagebionetworks.qtl.api.util;

import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.springframework.lang.Nullable;

/**
 * Checks that fields a mapper copies from a stored record are present, naming the record type in
 * the error so a data problem is traceable to its collection.
 */
public final class RequiredFieldValidator {

  private final String recordType;

  /**
   * @param recordType the type of record for error messages (e.g., "eQTL record")
   */
  public RequiredFieldValidator(String recordType) {
    this.recordType = recordType;
  }

  /**
   * Returns the value of a required field.
   *
   * @param <T> the field type
   * @param value the stored value (can be null)
   * @param fieldName the name of the field for error messages
   * @return the value, never null
   * @throws DataIntegrityException if the value is null
   */
  public <T> T require(@Nullable T value, String fieldName) {
    if (value == null) {
      throw new DataIntegrityException("Missing " + fieldName + " in " + recordType);
    }
    return value;
  }
}
