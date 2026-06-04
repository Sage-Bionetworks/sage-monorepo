package org.sagebionetworks.explorers;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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
   * Builds a cache key string from the provided components. The {@code filterType} is
   * accepted as {@code Object} so callers can pass any per-app generated enum (or string)
   * without coupling this helper to a specific OpenAPI-generated type.
   *
   * <p><strong>{@code toString()} contract:</strong> whatever {@code filterType.toString()}
   * returns is what lands in the cache key. OpenAPI-generated enums override {@code toString()}
   * to return the schema value (e.g. {@code "include"} / {@code "exclude"}), so passing the
   * enum directly produces keys keyed by the lowercase schema value. Plain Java enums without
   * a custom {@code toString()} fall back to {@link Enum#name()} ({@code "INCLUDE"} /
   * {@code "EXCLUDE"}) — callers wanting the schema-value convention should extract the
   * value to a {@code String} explicitly in that case.
   *
   * @param prefix the cache key prefix
   * @param filterType the filter type — its {@code toString()} is appended to the key (see contract above)
   * @param items the list of items to include in the key
   * @param extraParts additional parts to append to the key
   * @return the constructed cache key
   */
  public static String buildCacheKey(
    String prefix,
    @Nullable Object filterType,
    List<String> items,
    Object... extraParts
  ) {
    StringBuilder builder = new StringBuilder();

    builder
      .append(prefix)
      .append('-')
      .append(filterType != null ? filterType.toString() : "null")
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
   * Builds an {@code $addFields} stage that emits a {@code <field>_isEmpty} boolean for every sort
   * order in {@code sort}. The flag is {@code true} when the field is {@code null}, missing, an
   * empty string, or an empty array; {@code false} otherwise.
   *
   * <p>Prepending {@code <field>_isEmpty: 1} to the {@code $sort} doc (always ascending) before the
   * user's chosen sort key pushes empty/null rows to the tail regardless of the user's sort
   * direction, while non-empty rows sort normally within themselves.
   *
   * <p>When {@code aliases} is provided, the isEmpty expression is evaluated against the aliased
   * path rather than the raw sort field, so null detection targets the actual value being sorted
   * rather than a parent object that may be present even when the value itself is absent.
   *
   * @param sort the requested sort; if unsorted, returns {@code null}
   * @param aliases map from sort field name to the aliased document path used in {@code $sort}
   * @return an {@code $addFields} {@link AggregationOperation}, or {@code null} when sort is empty
   */
  public static AggregationOperation buildEmptyFlagFields(Sort sort, Map<String, String> aliases) {
    if (sort.isUnsorted()) {
      return null;
    }

    Document fields = new Document();
    for (Sort.Order order : sort) {
      String field = order.getProperty();
      String resolvedField = aliases.getOrDefault(field, field);

      Document isEmptyExpr = buildIsEmptyExpr(resolvedField);

      fields.append(isEmptyFlagKey(field), isEmptyExpr);
    }

    return context -> new Document("$addFields", fields);
  }

  /** Overload for callers with no sort-field aliases. */
  public static AggregationOperation buildEmptyFlagFields(Sort sort) {
    return buildEmptyFlagFields(sort, Map.of());
  }

  /**
   * Returns the {@code $addFields} output key for the isEmpty flag of the given sort field.
   * Spaces are replaced with underscores because spaces in {@code $addFields} key names are
   * unreliable in DocumentDB's {@code $sort} parser -- the flag would silently have no effect.
   */
  static String isEmptyFlagKey(String field) {
    return field.replace(" ", "_") + "_isEmpty";
  }

  /**
   * Builds an isEmpty expression for the given resolved field path. The flag is {@code true} when
   * the field is null, missing, an empty string, or an empty array.
   *
   * <p>For field paths containing no spaces, uses {@code "$field"} expression syntax directly.
   * For field paths containing spaces (e.g. {@code "4 months.log2_fc"}), {@code "$field"}
   * expression syntax silently fails to resolve the name, so {@code $getField} is used instead,
   * and the field access expression is bound to {@code $$val} via {@code $let} so all three
   * isEmpty checks can reference it uniformly. The null/missing branch uses {@code $type} rather
   * than {@code $eq null} because {@code $getField} returns {@code $$REMOVE} (not {@code null})
   * when the parent field is absent, and {@code $eq [$$REMOVE, null]} evaluates to {@code false}.
   *
   * <p>Spaced paths support one level of nesting by splitting on the first dot. Deeper nesting
   * would require additional chained {@code $getField} calls.
   */
  private static Document buildIsEmptyExpr(String resolvedField) {
    if (resolvedField.contains(" ")) {
      int dotIndex = resolvedField.indexOf('.');
      Object fieldAccess;
      if (dotIndex >= 0) {
        String parent = resolvedField.substring(0, dotIndex);
        String child = resolvedField.substring(dotIndex + 1);
        fieldAccess = new Document(
          "$getField",
          new Document("field", child).append("input", new Document("$getField", parent))
        );
      } else {
        fieldAccess = new Document("$getField", resolvedField);
      }

      // null/missing: $type returns "null" or "missing" for absent/null; $eq null fails for
      // $$REMOVE so $type is required here
      Document isNullOrMissing = new Document(
        "$in", List.of(new Document("$type", "$$val"), List.of("null", "missing"))
      );
      // empty string
      Document isEmptyString = new Document("$eq", List.of("$$val", ""));
      // empty array: $cond guards with $isArray so non-array fields return -1 (not 0)
      Document isEmptyArray = new Document(
        "$eq",
        List.of(
          new Document("$cond", List.of(
            new Document("$isArray", "$$val"),
            new Document("$size", "$$val"),
            -1
          )),
          0
        )
      );

      return new Document("$let", new Document()
        .append("vars", new Document("val", fieldAccess))
        .append("in", new Document("$or", List.of(isNullOrMissing, isEmptyString, isEmptyArray)))
      );
    }

    // null or missing -- ArrayList because List.of() prohibits null elements
    List<Object> isNullArgs = new ArrayList<>(2);
    isNullArgs.add("$" + resolvedField);
    isNullArgs.add(null);

    // empty string
    List<Object> isEmptyStringArgs = List.of("$" + resolvedField, "");

    // empty array: $cond guards with $isArray so non-array fields return -1 (not 0)
    Document isEmptyArrayExpr = new Document(
      "$eq",
      List.of(
        new Document("$cond", List.of(
          new Document("$isArray", "$" + resolvedField),
          new Document("$size", "$" + resolvedField),
          -1
        )),
        0
      )
    );

    return new Document("$or", List.of(
      new Document("$eq", isNullArgs),
      new Document("$eq", isEmptyStringArgs),
      isEmptyArrayExpr
    ));
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
