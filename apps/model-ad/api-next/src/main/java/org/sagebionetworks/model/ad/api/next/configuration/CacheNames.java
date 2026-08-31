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
   * Cache for marmoset model overview queries.
   * Stores results from marmoset model overview comparison queries filtered by items and filter
   * type.
   * Key format: Dynamic based on query parameters (items, filterType)
   */
  public static final String MARMOSET_MODEL_OVERVIEW = "marmosetModelOverview";

  /**
   * Cache for mouse model overview queries.
   * Stores results from mouse model overview comparison queries filtered by items and filter type.
   * Key format: Dynamic based on query parameters (items, filterType)
   */
  public static final String MOUSE_MODEL_OVERVIEW = "mouseModelOverview";

  /**
   * Cache for transcriptomics queries.
   * Stores results from transcriptomics comparison queries filtered by tissue, items,
   * and filter type.
   * Key format: Dynamic based on query parameters (tissue, items, filterType)
   */
  public static final String TRANSCRIPTOMICS = "transcriptomics";

  /**
   * Cache for transcriptomics individual queries.
   * Stores results from transcriptomics individual queries filtered by tissue,
   * modelIdentifier, modelIdentifierType, and ensemblGeneId.
   * Key format: Dynamic based on query parameters (tissue, modelIdentifier,
   * modelIdentifierType, ensemblGeneId)
   */
  public static final String TRANSCRIPTOMICS_INDIVIDUAL = "transcriptomicsIndividual";

  /**
   * Cache for model queries.
   * Stores results from model detail queries by name.
   * Key format: Dynamic based on model name
   */
  public static final String MODEL = "model";

  /**
   * Cache for comparison tool config queries.
   * Stores the comparison tool config information.
   * Key format: Static key "comparisonToolConfig"
   */
  public static final String COMPARISON_TOOL_CONFIG = "comparisonToolConfig";

  /**
   * Cache for model search queries.
   * Stores results from model search queries filtered by query string and organisms.
   * Key format: Dynamic based on query parameters (query, modelOrganisms)
   */
  public static final String MODEL_SEARCH = "modelSearch";
}
