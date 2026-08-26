package org.sagebionetworks.model.ad.api.next.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Represents a composite identifier for transcriptomics documents.
 * Format: ensembl_gene_id~name~sex (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female")
 */
@Value
@Builder
@Getter
public class TranscriptomicsIdentifier {

  // MG-586 - Consider using existing CompositeIdentifier utility if applicable
  String ensemblGeneId;
  String name;
  String sex;

  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 3;

  /**
   * Parses a composite identifier string into a TranscriptomicsIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static TranscriptomicsIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, -1); // -1 to include trailing empty strings

    if (parts.length != EXPECTED_PARTS) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. Expected format: 'ensembl_gene_id~name~sex' (e.g., 'ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)~Female')",
          compositeId
        )
      );
    }

    String ensemblGeneId = parts[0].trim();
    String name = parts[1].trim();
    String sex = parts[2].trim();

    if (ensemblGeneId.isEmpty() || name.isEmpty() || sex.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (ensembl_gene_id, name, sex) must be non-empty",
          compositeId
        )
      );
    }

    return TranscriptomicsIdentifier.builder()
      .ensemblGeneId(ensemblGeneId)
      .name(name)
      .sex(sex)
      .build();
  }

  /**
   * Returns the composite identifier as a string.
   *
   * @return the composite identifier string
   */
  public String toCompositeId() {
    return ensemblGeneId + DELIMITER + name + DELIMITER + sex;
  }

  /**
   * Converts this identifier to a MongoDB {@link Criteria}
   * that matches documents with this exact ensembl_gene_id, name.link_text, and sex.
   *
   * @return a Criteria requiring all fields to match
   */
  public Criteria toCriteria() {
    return new Criteria()
      .andOperator(
        Criteria.where("ensembl_gene_id").is(ensemblGeneId),
        Criteria.where("name.link_text").is(name),
        Criteria.where("sex").is(sex)
      );
  }
}
