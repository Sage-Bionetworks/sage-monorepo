package org.sagebionetworks.model.ad.api.next.util;

import java.util.function.Function;
import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.dto.SexCohortDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
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
   * @param recordType the type of record for error messages (e.g., "gene expression record")
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
      throw new DataIntegrityException("Missing " + fieldName + " value in " + recordType);
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

  /**
   * Converts a nullable string value to SexDto enum.
   * Convenience method for sex field conversions.
   *
   * @param value the string value to convert (can be null)
   * @param recordType the type of record for error messages (e.g., "disease correlation record")
   * @return the converted SexDto value
   * @throws DataIntegrityException if value is null or conversion fails
   */
  public static SexDto toSexDto(@Nullable String value, String recordType) {
    return toEnum(value, SexDto::fromValue, "sex", recordType);
  }

  /**
   * Converts a nullable string value to SexCohortDto enum.
   * Convenience method for sex cohort field conversions.
   *
   * @param value the string value to convert (can be null)
   * @param recordType the type of record for error messages (e.g., "gene expression record")
   * @return the converted SexCohortDto value
   * @throws DataIntegrityException if value is null or conversion fails
   */
  public static SexCohortDto toSexCohortDto(@Nullable String value, String recordType) {
    return toEnum(value, SexCohortDto::fromValue, "sexCohort", recordType);
  }
}
