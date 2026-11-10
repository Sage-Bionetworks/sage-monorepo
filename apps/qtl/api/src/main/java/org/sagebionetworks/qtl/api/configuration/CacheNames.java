package org.sagebionetworks.qtl.api.configuration;

/**
 * Constants for cache names used in the QTL API service.
 */
public final class CacheNames {

  private CacheNames() {
    // Utility class, prevent instantiation
  }

  /**
   * Cache for comparison tool config queries.
   * Stores the comparison tool config information.
   * Key format: Static key "comparisonToolConfig"
   */
  public static final String COMPARISON_TOOL_CONFIG = "comparisonToolConfig";

  /**
   * Cache for eQTL comparison tool queries.
   * Key format: "eqtl-{filterType}-{items}-{search}-{filters}-{page}-{size}-{budget}-{sort}"
   */
  public static final String EQTL = "eqtl";
}
