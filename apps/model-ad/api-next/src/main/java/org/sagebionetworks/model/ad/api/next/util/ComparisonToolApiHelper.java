package org.sagebionetworks.model.ad.api.next.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public final class ComparisonToolApiHelper {

  private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
  public static final String CATEGORY_REQUIREMENT_MESSAGE =
    "Query parameter category must repeat twice (e.g. ?category=CONSENSUS NETWORK MODULES" +
    "&category=subcategory) and each value must be a string";

  private ComparisonToolApiHelper() {}

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
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
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

  public static Map<String, Object> buildProblemJson(
    HttpStatus status,
    String title,
    String detail,
    String instance
  ) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("title", title);
    body.put("status", status.value());
    body.put("detail", detail);
    body.put("instance", instance);
    return body;
  }
}
