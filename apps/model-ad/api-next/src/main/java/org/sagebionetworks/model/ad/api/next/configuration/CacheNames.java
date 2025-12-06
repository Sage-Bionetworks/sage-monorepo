package org.sagebionetworks.model.ad.api.next.configuration;

/**
 * Constants for cache names used in the Model-AD API service.
 */
public final class CacheNames {

  private CacheNames() {
    // Utility class, prevent instantiation
  }

  /**
   * Cache for disease correlation queries.
   * Stores results from disease correlation comparison queries filtered by cluster, items, and
   * filter type.
   * Key format: Dynamic based on query parameters (cluster, items, filterType)
   */
  public static final String DISEASE_CORRELATION = "diseaseCorrelation";

  /**
   * Cache for model overview queries.
   * Stores results from model overview comparison queries filtered by items and filter type.
   * Key format: Dynamic based on query parameters (items, filterType)
   */
  public static final String MODEL_OVERVIEW = "modelOverview";

  /**
   * Cache for gene expression queries.
   * Stores results from gene expression comparison queries filtered by tissue, sex, items, and
   * filter type.
   * Key format: Dynamic based on query parameters (tissue, sex, items, filterType)
   */
  public static final String GENE_EXPRESSION = "geneExpression";
}
