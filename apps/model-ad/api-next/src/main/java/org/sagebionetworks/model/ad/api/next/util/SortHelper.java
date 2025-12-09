package org.sagebionetworks.model.ad.api.next.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.domain.Sort;

/**
 * Utility class for building Spring Data Sort objects with field-specific transformations.
 * Centralizes sort validation and construction logic used across comparison tool services.
 */
public final class SortHelper {

  // Pre-compiled regex patterns for performance
  private static final Pattern TIME_PERIOD_PATTERN = Pattern.compile("^\\d+\\s+months?$");
  private static final Pattern BRAIN_REGION_PATTERN = Pattern.compile(
    "^(CBE|DLPFC|FP|IFG|PHG|STG|TCX)$"
  );

  private SortHelper() {}

  /**
   * Builds a Spring Data Sort object with field-specific transformation.
   *
   * @param sortFields list of field names to sort by
   * @param sortOrders list of sort directions (1 for ascending, -1 for descending)
   * @param fieldTransformer function to transform field names (e.g., append nested field paths)
   * @return Sort object for use in PageRequest
   * @throws IllegalArgumentException if arrays have mismatched lengths or invalid values
   */
  public static Sort buildSort(
    List<String> sortFields,
    List<Integer> sortOrders,
    FieldTransformer fieldTransformer
  ) {
    if (sortFields == null || sortFields.isEmpty()) {
      return Sort.unsorted();
    }
    if (sortOrders == null || sortOrders.isEmpty()) {
      return Sort.unsorted();
    }

    validateSortParameters(sortFields, sortOrders);

    List<Sort.Order> orders = new ArrayList<>(sortFields.size());
    for (int i = 0; i < sortFields.size(); i++) {
      String field = sortFields.get(i);
      Integer order = sortOrders.get(i);

      validateSortOrder(order);
      Sort.Direction direction = order == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;

      String transformedField = fieldTransformer.transform(field);
      Sort.Order sortOrder = new Sort.Order(direction, transformedField).nullsLast();
      orders.add(sortOrder);
    }

    return Sort.by(orders);
  }

  /**
   * Validates that sortFields and sortOrders arrays have matching lengths.
   *
   * @throws IllegalArgumentException if arrays have different lengths
   */
  private static void validateSortParameters(List<String> sortFields, List<Integer> sortOrders) {
    if (sortFields.size() != sortOrders.size()) {
      throw new IllegalArgumentException(
        "sortFields and sortOrders must have the same length. Got sortFields: " +
        sortFields.size() +
        ", sortOrders: " +
        sortOrders.size()
      );
    }
  }

  /**
   * Validates that a sort order value is either 1 (ascending) or -1 (descending).
   *
   * @throws IllegalArgumentException if order is not 1 or -1
   */
  private static void validateSortOrder(Integer order) {
    if (order != 1 && order != -1) {
      throw new IllegalArgumentException(
        "sortOrders must contain only 1 (ascending) or -1 (descending). Got: " + order
      );
    }
  }

  /**
   * Functional interface for transforming field names before sorting.
   */
  @FunctionalInterface
  public interface FieldTransformer {
    /**
     * Transforms a field name (e.g., appends nested object path).
     *
     * @param field the original field name
     * @return the transformed field name for MongoDB sorting
     */
    String transform(String field);
  }

  /**
   * No-op transformer that returns the field name unchanged.
   */
  public static final FieldTransformer NO_TRANSFORM = field -> field;

  /**
   * Transformer for Gene Expression fields.
   * Appends ".log2_fc" to time period fields (e.g., "4 months" -> "4 months.log2_fc").
   */
  public static final FieldTransformer GENE_EXPRESSION_TRANSFORMER = field -> {
    if (TIME_PERIOD_PATTERN.matcher(field).matches()) {
      return field + ".log2_fc";
    }
    return field;
  };

  /**
   * Transformer for Disease Correlation fields.
   * Appends ".correlation" to brain region fields (e.g., "PHG" -> "PHG.correlation").
   */
  public static final FieldTransformer DISEASE_CORRELATION_TRANSFORMER = field -> {
    if (BRAIN_REGION_PATTERN.matcher(field).matches()) {
      return field + ".correlation";
    }
    return field;
  };
}
