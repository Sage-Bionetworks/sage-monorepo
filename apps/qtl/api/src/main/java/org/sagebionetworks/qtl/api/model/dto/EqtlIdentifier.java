package org.sagebionetworks.qtl.api.model.dto;

import lombok.Builder;
import lombok.Value;
import org.sagebionetworks.qtl.api.exception.InvalidFilterException;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Represents a composite identifier for eQTL documents.
 * Format: variant~ensembl_gene_id~cell_type_id (e.g., "rs12655812~ENSG00000250891~astro")
 */
@Value
@Builder
public class EqtlIdentifier {

  String variant;
  String ensemblGeneId;
  String cellTypeId;

  private static final String DELIMITER = "~";
  private static final int EXPECTED_PARTS = 3;

  /**
   * Parses a composite identifier string into an EqtlIdentifier.
   *
   * @param compositeId the composite identifier string (e.g., "rs12655812~ENSG00000250891~astro")
   * @return the parsed identifier
   * @throws InvalidFilterException if the format is invalid
   */
  public static EqtlIdentifier parse(String compositeId) {
    if (compositeId == null || compositeId.isBlank()) {
      throw new InvalidFilterException("Composite identifier cannot be null or empty");
    }

    String[] parts = compositeId.split(DELIMITER, -1);

    if (parts.length != EXPECTED_PARTS) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier format: '%s'. Expected format: 'variant~ensembl_gene_id~cell_type_id' (e.g., 'rs12655812~ENSG00000250891~astro')",
          compositeId
        )
      );
    }

    String variant = parts[0].trim();
    String ensemblGeneId = parts[1].trim();
    String cellTypeId = parts[2].trim();

    if (variant.isEmpty() || ensemblGeneId.isEmpty() || cellTypeId.isEmpty()) {
      throw new InvalidFilterException(
        String.format(
          "Invalid composite identifier: '%s'. All parts (variant, ensembl_gene_id, cell_type_id) must be non-empty",
          compositeId
        )
      );
    }

    return EqtlIdentifier.builder()
      .variant(variant)
      .ensemblGeneId(ensemblGeneId)
      .cellTypeId(cellTypeId)
      .build();
  }

  /**
   * Returns the composite identifier as a string.
   *
   * @return the composite identifier string (e.g., "rs12655812~ENSG00000250891~astro")
   */
  public String toCompositeId() {
    return variant + DELIMITER + ensemblGeneId + DELIMITER + cellTypeId;
  }

  /**
   * Converts this identifier to a MongoDB {@link Criteria}
   * that matches documents with this exact variant, Ensembl gene ID, and cell type ID.
   *
   * @return a Criteria requiring all three fields to match
   */
  public Criteria toCriteria() {
    return new Criteria()
      .andOperator(
        Criteria.where("variant").is(variant),
        Criteria.where("ensembl_gene_id").is(ensemblGeneId),
        Criteria.where("cell_type_id").is(cellTypeId)
      );
  }
}
