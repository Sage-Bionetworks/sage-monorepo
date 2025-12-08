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

  private ApiHelper() {}

  public static HttpHeaders createNoCacheHeaders(MediaType mediaType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl(CACHE_CONTROL_VALUE);
    headers.setPragma("no-cache");
    headers.setExpires(0);
    headers.setContentType(mediaType);
    return headers;
  }

  public static List<String> sanitizeItems(@Nullable List<String> rawItems) {
    if (rawItems == null) {
      return List.of();
    }
    return rawItems.stream().filter(Objects::nonNull).toList();
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

  public static String buildCacheKey(
    String prefix,
    ItemFilterTypeQueryDto filterType,
    List<String> items,
    Object... extraParts
  ) {
    StringBuilder builder = new StringBuilder(prefix)
      .append('-')
      .append(filterType != null ? filterType.getValue() : "null")
      .append('-')
      .append(items);

    if (extraParts != null && extraParts.length > 0) {
      Arrays.stream(extraParts).forEach(part -> builder.append('-').append(Objects.toString(part)));
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
