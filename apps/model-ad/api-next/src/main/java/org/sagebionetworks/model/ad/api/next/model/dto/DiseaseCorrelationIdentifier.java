package org.sagebionetworks.model.ad.api.next.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;

/**
 * Represents a composite identifier for disease correlation documents.
 * Format: name~age~sex (e.g., "APOE4~4 months~Female")
 */
@Value
@Builder
@Getter
public class DiseaseCorrelationIdentifier {

  String name;
  String age;
  String sex;

  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 3;

  /**
   * Parses a composite identifier string into a DiseaseCorrelationIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "APOE4~4 months~Female")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static DiseaseCorrelationIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, EXPECTED_PARTS);

    if (parts.length != EXPECTED_PARTS) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. Expected format: 'name~age~sex' (e.g., 'APOE4~4 months~Female')",
          compositeId
        )
      );
    }

    String name = parts[0].trim();
    String age = parts[1].trim();
    String sex = parts[2].trim();

    if (name.isEmpty() || age.isEmpty() || sex.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (name, age, sex) must be non-empty",
          compositeId
        )
      );
    }

    return DiseaseCorrelationIdentifier.builder().name(name).age(age).sex(sex).build();
  }

  /**
   * Converts this identifier to a composite string.
   *
   * @return the composite identifier string (e.g., "APOE4~4 months~Female")
   */
  public String toCompositeString() {
    return name + DELIMITER + age + DELIMITER + sex;
  }
}
