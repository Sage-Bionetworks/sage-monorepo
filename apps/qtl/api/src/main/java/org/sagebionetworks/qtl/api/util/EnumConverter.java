package org.sagebionetworks.qtl.api.util;

import java.util.function.Function;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.springframework.lang.Nullable;

/**
 * Utility class for safely converting string values to enum types with proper error handling.
 */
public final class EnumConverter {

  private EnumConverter() {
    // Utility class
  }

  /**
   * Converts a nullable string value to an enum using the provided conversion function.
   * Throws DataIntegrityException if the value is null or cannot be converted.
   *
   * @param <T> the enum type
   * @param value the string value to convert (can be null)
   * @param converter the function to convert string to enum (e.g., EnumType::fromValue)
   * @param fieldName the name of the field for error messages
   * @param recordType the type of record for error messages (e.g., "transcriptomics record")
   * @return the converted enum value
   * @throws DataIntegrityException if value is null or conversion fails
   */
  public static <T> T toEnum(
    @Nullable String value,
    Function<String, T> converter,
    String fieldName,
    String recordType
  ) {
    if (value == null) {
      throw new DataIntegrityException("Missing " + fieldName + " in " + recordType);
    }
    try {
      return converter.apply(value);
    } catch (IllegalArgumentException ex) {
      throw new DataIntegrityException(
        "Unexpected " + fieldName + " value '" + value + "' in " + recordType,
        ex
      );
    }
  }
}
