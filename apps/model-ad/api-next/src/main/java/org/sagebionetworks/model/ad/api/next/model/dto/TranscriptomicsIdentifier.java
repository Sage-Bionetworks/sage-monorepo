package org.sagebionetworks.model.ad.api.next.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;

/**
 * Represents a composite identifier for transcriptomics documents.
 * Format: ensembl_gene_id~name (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)")
 */
@Value
@Builder
@Getter
public class TranscriptomicsIdentifier {

  // MG-586 - Consider using existing CompositeIdentifier utility if applicable
  String ensemblGeneId;
  String name;

  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 2;

  /**
   * Parses a composite identifier string into a TranscriptomicsIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)")
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
          "Invalid composite identifier format: '%s'. Expected format: 'ensembl_gene_id~name' (e.g., 'ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)')",
          compositeId
        )
      );
    }

    String ensemblGeneId = parts[0].trim();
    String name = parts[1].trim();

    if (ensemblGeneId.isEmpty() || name.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (ensembl_gene_id, name) must be non-empty",
          compositeId
        )
      );
    }

    return TranscriptomicsIdentifier.builder().ensemblGeneId(ensemblGeneId).name(name).build();
  }

  /**
   * Returns the composite identifier as a string.
   *
   * @return the composite identifier string
   */
  public String toCompositeId() {
    return String.format("%s~%s", ensemblGeneId, name);
  }

  /**
   * Converts this identifier to a MongoDB {@link org.springframework.data.mongodb.core.query.Criteria}
   * that matches documents with this exact ensembl_gene_id and name.link_text.
   *
   * @return a Criteria requiring both fields to match
   */
  public org.springframework.data.mongodb.core.query.Criteria toCriteria() {
    return new org.springframework.data.mongodb.core.query.Criteria()
      .andOperator(
        org.springframework.data.mongodb.core.query.Criteria.where("ensembl_gene_id").is(ensemblGeneId),
        org.springframework.data.mongodb.core.query.Criteria.where("name.link_text").is(name)
      );
  }
}
