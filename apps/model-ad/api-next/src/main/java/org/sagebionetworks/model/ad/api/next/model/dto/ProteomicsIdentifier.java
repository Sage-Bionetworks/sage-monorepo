package org.sagebionetworks.model.ad.api.next.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Represents a composite identifier for proteomics documents.
 * Format: unique_id~name~sex (e.g., "ENSMUSG00000000001P27144~LOAD2~Female")
 */
@Value
@Builder
@Getter
public class ProteomicsIdentifier {

  // MG-586 - Consider using existing CompositeIdentifier utility if applicable
  String uniqueId;
  String name;
  String sex;

  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 3;

  /**
   * Parses a composite identifier string into a ProteomicsIdentifier.
   *
   * @param compositeId the composite identifier string
   *                    (e.g., "ENSMUSG00000000001P27144~LOAD2~Female")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static ProteomicsIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, -1); // -1 to include trailing empty strings

    if (parts.length != EXPECTED_PARTS) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. Expected format: 'unique_id~name~sex' " +
          "(e.g., 'ENSMUSG00000000001P27144~LOAD2~Female')",
          compositeId
        )
      );
    }

    String uniqueId = parts[0].trim();
    String name = parts[1].trim();
    String sex = parts[2].trim();

    if (uniqueId.isEmpty() || name.isEmpty() || sex.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (unique_id, name, sex) must be non-empty",
          compositeId
        )
      );
    }

    return ProteomicsIdentifier.builder().uniqueId(uniqueId).name(name).sex(sex).build();
  }

  /**
   * Returns the composite identifier as a string.
   *
   * @return the composite identifier string
   */
  public String toCompositeId() {
    return uniqueId + DELIMITER + name + DELIMITER + sex;
  }

  /**
   * Converts this identifier to a MongoDB {@link Criteria}
   * that matches documents with this exact unique_id, name.link_text, and sex.
   *
   * @return a Criteria requiring all fields to match
   */
  public Criteria toCriteria() {
    return new Criteria()
      .andOperator(
        Criteria.where("unique_id").is(uniqueId),
        Criteria.where("name.link_text").is(name),
        Criteria.where("sex").is(sex)
      );
  }
}
