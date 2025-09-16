package org.sagebionetworks.openchallenges.api.gateway.routing;

import java.util.Objects;

/**
 * Strongly typed key for a route: HTTP method + normalized path.
 *
 * Prevents mistakes like casing or extra spaces in lookups.
 */
public record RouteKey(String method, String path) {
  public RouteKey {
    Objects.requireNonNull(method, "method must not be null");
    Objects.requireNonNull(path, "path must not be null");
    method = method.trim().toUpperCase();
    path = normalizePath(path);
  }

  public static RouteKey of(String method, String path) {
    return new RouteKey(method, path);
  }

  private static String normalizePath(String raw) {
    String p = raw.trim();
    if (p.isEmpty()) throw new IllegalArgumentException("path must not be blank");

    // collapse multiple slashes
    p = p.replaceAll("/{2,}", "/");
    // ensure leading slash
    if (!p.startsWith("/")) p = "/" + p;
    // remove trailing slash except for root
    if (p.length() > 1 && p.endsWith("/")) {
      p = p.substring(0, p.length() - 1);
    }
    return p;
  }
}
