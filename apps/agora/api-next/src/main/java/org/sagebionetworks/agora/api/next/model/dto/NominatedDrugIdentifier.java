package org.sagebionetworks.agora.api.next.model.dto;

import lombok.Builder;
import lombok.Value;
import org.sagebionetworks.agora.api.next.exception.InvalidFilterException;

/**
 * Represents a composite identifier for nominated drug documents.
 * Format: chembl_id~combined_with (e.g., "CHEMBL2105758~null")
 */
@Value
@Builder
public class NominatedDrugIdentifier {

  String chemblId;
  String combinedWith;

  // Using '~' as delimiter — unlikely in drug names but would break parsing if present
  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 2;

  /**
   * Parses a composite identifier string into a NominatedDrugIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "CHEMBL2105758~null")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static NominatedDrugIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, -1);

    if (parts.length != EXPECTED_PARTS) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. "
            + "Expected format: 'chembl_id~combined_with' "
            + "(e.g., 'CHEMBL2105758~null')",
          compositeId
        )
      );
    }

    String chemblId = parts[0].trim();
    String combinedWith = parts[1].trim();

    if (chemblId.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. chembl_id must be non-empty",
          compositeId
        )
      );
    }

    return NominatedDrugIdentifier.builder()
      .chemblId(chemblId)
      .combinedWith("null".equals(combinedWith) ? null : combinedWith)
      .build();
  }

  /**
   * Returns the composite identifier as a string.
   *
   * @return the composite identifier string
   */
  public String toCompositeId() {
    return String.format("%s~%s", chemblId, combinedWith);
  }
}
