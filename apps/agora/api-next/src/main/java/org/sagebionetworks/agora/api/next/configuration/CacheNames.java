package org.sagebionetworks.agora.api.next.configuration;

/**
 * Constants for cache names used in the Agora API service.
 */
public final class CacheNames {

  private CacheNames() {
    // Utility class, prevent instantiation
  }

  /**
   * Cache for data version queries.
   * Stores the data version information from Synapse.
   * Key format: Static key "dataVersion"
   */
  public static final String DATA_VERSION = "dataVersion";

  /**
   * Cache for comparison tool config queries.
   * Stores the comparison tool config information.
   * Key format: Static key "comparisonToolConfig"
   */
  public static final String COMPARISON_TOOL_CONFIG = "comparisonToolConfig";

  /**
   * Cache for nominated target queries.
   * Stores results from nominated target comparison queries filtered by items and filter type.
   * Key format: Dynamic based on query parameters (items, filterType)
   */
  public static final String NOMINATED_TARGET = "nominatedTarget";
}
