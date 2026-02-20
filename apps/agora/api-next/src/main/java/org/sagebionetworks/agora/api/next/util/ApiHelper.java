package org.sagebionetworks.agora.api.next.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public final class ApiHelper {

  private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";

  private ApiHelper() {}

  /**
   * Validates that sortFields and sortOrders have matching element counts.
   * Note: Presence and non-emptiness are enforced by OpenAPI schema validation
   * (@NotNull @Size(min = 1)), so this method only validates array length matching.
   *
   * @param sortFields List of sort field names (required, non-empty per OpenAPI schema)
   * @param sortOrders List of sort orders (required, non-empty per OpenAPI schema)
   * @throws IllegalArgumentException if arrays have different lengths
   */
  public static void validateSortParameters(List<String> sortFields, List<?> sortOrders) {
    int fieldCount = sortFields.size();
    int orderCount = sortOrders.size();

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
   * Creates a Spring Data Sort object from sortFields and sortOrders lists.
   * Both parameters are required and must be non-empty (enforced by OpenAPI schema validation).
   * Validates that sortFields and sortOrders have matching element counts.
   *
   * @param sortFields list of field names to sort by (required, non-empty)
   * @param sortOrders list of sort orders (required, non-empty, 1 for ascending, -1 for descending)
   * @return Sort object for use with Spring Data repositories
   * @throws IllegalArgumentException if sortFields and sortOrders have different lengths
   */
  public static Sort createSort(List<String> sortFields, List<Integer> sortOrders) {
    validateSortParameters(sortFields, sortOrders);

    Sort sort = Sort.unsorted();
    for (int i = 0; i < sortFields.size(); i++) {
      String field = sortFields.get(i);
      Integer order = sortOrders.get(i);
      Sort.Direction direction = (order != null && order < 0)
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      sort = sort.and(Sort.by(direction, field));
    }
    return sort;
  }

  /**
   * Sanitizes a list of items by filtering out null values.
   * Returns an empty list if the input is null.
   *
   * @param rawItems list of items to sanitize
   * @return list of non-null strings
   */
  public static List<String> sanitizeItems(@Nullable List<String> rawItems) {
    if (rawItems == null) {
      return List.of();
    }
    return rawItems.stream().filter(Objects::nonNull).toList();
  }

  public static HttpHeaders createNoCacheHeaders(MediaType mediaType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl(CACHE_CONTROL_VALUE);
    headers.setPragma("no-cache");
    headers.setExpires(0);
    headers.setContentType(mediaType);
    return headers;
  }

  /**
   * Builds a cache key string from the provided components.
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
    StringBuilder builder = new StringBuilder();

    builder
      .append(prefix)
      .append('-')
      .append(filterType != null ? filterType.getValue() : "null")
      .append('-')
      .append(items != null ? items : List.of());

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
  public static List<Pattern> createCaseInsensitiveFullMatchPatterns(String commaSeparatedNames) {
    return Arrays.stream(commaSeparatedNames.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .map(name -> Pattern.compile("^" + Pattern.quote(name) + "$", Pattern.CASE_INSENSITIVE))
      .toList();
  }

  /**
   * Validates that all query parameters in the current HTTP request are in the allowed set.
   * This method retrieves the current request from RequestContextHolder and checks each
   * query parameter against the provided set of valid parameter names.
   *
   * @param validQueryParams set of allowed query parameter names
   * @throws IllegalArgumentException if an unknown query parameter is encountered
   */
  public static void validateQueryParameters(Set<String> validQueryParams) {
    ServletRequestAttributes attributes =
      (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      log.debug("Skipping query parameter validation: RequestContextHolder returned null");
      return;
    }

    HttpServletRequest request = attributes.getRequest();
    Map<String, String[]> parameterMap = request.getParameterMap();

    log.debug("Validating query parameters: {}", parameterMap.keySet());

    for (String paramName : parameterMap.keySet()) {
      if (!validQueryParams.contains(paramName)) {
        log.warn("Invalid query parameter: '{}'", paramName);
        throw new IllegalArgumentException("Unknown query parameter: " + paramName);
      }
    }
  }
}
