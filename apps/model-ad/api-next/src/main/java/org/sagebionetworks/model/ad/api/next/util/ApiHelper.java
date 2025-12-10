package org.sagebionetworks.model.ad.api.next.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

public final class ApiHelper {

  private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";

  /**
   * Base buffer size for cache key construction to accommodate fixed string components
   * (prefix, separators, filterType, and list brackets).
   */
  private static final int BASE_CAPACITY_BUFFER = 50;

  /**
   * Estimated average size per extra part in cache key construction.
   * Used to pre-allocate StringBuilder capacity.
   */
  private static final int ESTIMATED_PART_SIZE = 20;

  private ApiHelper() {}

  /**
   * Validates that sortFields and sortOrders are both present and have matching element counts.
   *
   * @param sortFields Comma-delimited sort field names (required)
   * @param sortOrders Comma-delimited sort orders (required)
   * @throws IllegalArgumentException if validation fails
   */
  public static void validateSortParameters(String sortFields, String sortOrders) {
    boolean hasSortFields = sortFields != null && !sortFields.isBlank();
    boolean hasSortOrders = sortOrders != null && !sortOrders.isBlank();

    if (!hasSortFields) {
      throw new IllegalArgumentException("sortFields is required");
    }

    if (!hasSortOrders) {
      throw new IllegalArgumentException("sortOrders is required");
    }

    int fieldCount = sortFields.split(",", -1).length;
    int orderCount = sortOrders.split(",", -1).length;

    if (fieldCount != orderCount) {
      throw new IllegalArgumentException(
        String.format(
          "sortFields and sortOrders must have the same number of elements. " +
          "Got %d field(s) and %d order(s)",
          fieldCount,
          orderCount
        )
      );
    }
  }

  /**
   * Parses a comma-delimited string into a list of strings.
   * Returns an empty list if the input is null or empty.
   *
   * @param commaDelimited comma-separated string (e.g., "value1,value2,value3")
   * @return list of trimmed non-null strings
   */
  public static List<String> parseCommaDelimitedString(@Nullable String commaDelimited) {
    if (commaDelimited == null || commaDelimited.isBlank()) {
      return List.of();
    }
    return Arrays.stream(commaDelimited.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .toList();
  }

  /**
   * Parses a comma-delimited string of integers.
   * Returns an empty list if the input is null or empty.
   *
   * @param commaDelimited comma-separated integers (e.g., "1,-1,1")
   * @return list of integers
   * @throws NumberFormatException if any value cannot be parsed as an integer
   */
  public static List<Integer> parseCommaDelimitedIntegers(@Nullable String commaDelimited) {
    if (commaDelimited == null || commaDelimited.isBlank()) {
      return List.of();
    }
    return Arrays.stream(commaDelimited.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .map(Integer::parseInt)
      .toList();
  }

  public static HttpHeaders createNoCacheHeaders(MediaType mediaType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl(CACHE_CONTROL_VALUE);
    headers.setPragma("no-cache");
    headers.setExpires(0);
    headers.setContentType(mediaType);
    return headers;
  }

  public static List<ObjectId> parseObjectIds(List<String> items) {
    try {
      return items.stream().map(ObjectId::new).toList();
    } catch (IllegalArgumentException ex) {
      throw new InvalidObjectIdException(
        "Query parameter item must contain valid ObjectId values",
        ex
      );
    }
  }

  /**
   * Builds a cache key string from the provided components.
   * Optimized to avoid unnecessary object allocations.
   *
   * @param prefix the cache key prefix
   * @param filterType the filter type (can be null)
   * @param items the list of items to include in the key
   * @param extraParts additional parts to append to the key
   * @return the constructed cache key
   */
  public static String buildCacheKey(
    String prefix,
    ItemFilterTypeQueryDto filterType,
    List<String> items,
    Object... extraParts
  ) {
    // Pre-calculate capacity to avoid StringBuilder reallocations during building
    int capacity =
      prefix.length() +
      BASE_CAPACITY_BUFFER +
      (extraParts != null ? extraParts.length * ESTIMATED_PART_SIZE : 0);
    StringBuilder builder = new StringBuilder(capacity);

    builder
      .append(prefix)
      .append('-')
      .append(filterType != null ? filterType.getValue() : "null")
      .append('-');

    // Manually build list representation to avoid creating intermediate String from List.toString()
    // This directly appends each item to StringBuilder instead of:
    //   1. Calling items.toString() which creates "[item1, item2, item3]" string
    //   2. Then copying that string into StringBuilder
    if (items != null && !items.isEmpty()) {
      builder.append('[');
      for (int i = 0; i < items.size(); i++) {
        if (i > 0) {
          builder.append(',');
        }
        builder.append(items.get(i));
      }
      builder.append(']');
    } else {
      builder.append("[]");
    }

    // Use simple for-each instead of Stream to avoid Stream object allocation
    if (extraParts != null) {
      for (Object part : extraParts) {
        builder.append('-').append(Objects.toString(part));
      }
    }

    return builder.toString();
  }

  /**
   * Creates case-insensitive full match regex patterns from comma-separated names.
   *
   * @param commaSeparatedNames comma-separated list of names
   * @return list of compiled regex patterns for case-insensitive exact matching
   */
  public static List<Pattern> createCaseInsensitiveFullMatchPatterns(
    String commaSeparatedNames
  ) {
    return Arrays.stream(commaSeparatedNames.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .map(name -> Pattern.compile("^" + Pattern.quote(name) + "$", Pattern.CASE_INSENSITIVE))
      .toList();
  }
}
