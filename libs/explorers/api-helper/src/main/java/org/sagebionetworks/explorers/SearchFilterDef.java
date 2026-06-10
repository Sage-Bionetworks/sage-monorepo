package org.sagebionetworks.explorers;

/**
 * Defines the field used for comparison-tool search filtering.
 *
 * <p>Search applies only in EXCLUDE mode (when {@code itemFilterType == EXCLUDE}). Two patterns:
 *
 * <ul>
 *   <li>Comma-separated search terms → exact match (case-insensitive) via
 *       {@link ApiHelper#createCaseInsensitiveFullMatchPatterns}
 *   <li>Single search term → partial match (case-insensitive regex)
 * </ul>
 *
 * @param field the MongoDB field name to search against (e.g. "name", "gene_symbol")
 */
public record SearchFilterDef(String field) {}
