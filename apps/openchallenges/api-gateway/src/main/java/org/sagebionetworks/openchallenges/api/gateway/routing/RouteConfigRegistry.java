package org.sagebionetworks.openchallenges.api.gateway.routing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteConfig;

/** Immutable, thread-safe registry of route configurations. */
public final class RouteConfigRegistry {

  private final Map<RouteKey, RouteConfig> routeConfigs;

  public RouteConfigRegistry() {
    this.routeConfigs = Map.of();
  }

  public RouteConfigRegistry(Map<RouteKey, RouteConfig> routeConfigs) {
    if (routeConfigs == null || routeConfigs.isEmpty()) {
      this.routeConfigs = Map.of();
    } else {
      Map<RouteKey, RouteConfig> copy = new LinkedHashMap<>(routeConfigs.size());
      routeConfigs.forEach((k, v) -> {
        if (k == null || v == null) {
          throw new IllegalArgumentException("RouteKey and RouteConfig must not be null");
        }
        copy.put(k, v);
      });
      this.routeConfigs = Collections.unmodifiableMap(copy);
    }
  }

  public Optional<RouteConfig> getRouteConfig(String method, String path) {
    if (method == null || path == null) {
      return Optional.empty();
    }

    // First attempt: exact match (fast path)
    RouteConfig direct = routeConfigs.get(RouteKey.of(method, path));
    if (direct != null) {
      return Optional.of(direct);
    }

    // Fallback: pattern match where configured route contains path variables like {id}
    String normalizedIncomingPath = normalizePath(path);
    String normalizedMethod = method.trim().toUpperCase();

    for (Map.Entry<RouteKey, RouteConfig> entry : routeConfigs.entrySet()) {
      RouteKey key = entry.getKey();
      if (!key.method().equals(normalizedMethod)) {
        continue; // method must match exactly
      }
      String candidatePattern = key.path();
      if (!candidatePattern.contains("{")) {
        continue; // already tried exact matches above; skip non-parameterized patterns
      }
      if (pathPatternMatches(candidatePattern, normalizedIncomingPath)) {
        return Optional.of(entry.getValue());
      }
    }

    // HEAD request fallback: treat HEAD as GET for route configuration lookup
    // HTTP HEAD requests should have the same behavior as GET (authentication, authorization)
    // but without a response body
    if ("HEAD".equals(normalizedMethod)) {
      return getRouteConfig("GET", path);
    }

    return Optional.empty();
  }

  public Set<String> getScopesForRoute(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::scopes).orElse(Set.of());
  }

  public Optional<String> getAudienceForRoute(String method, String path) {
    return getRouteConfig(method, path)
      .map(RouteConfig::audience)
      .filter(a -> a != null && !a.isBlank());
  }

  public boolean isAnonymousAccessAllowed(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::anonymousAccess).orElse(false);
  }

  public Map<RouteKey, RouteConfig> getAllRouteConfigs() {
    return routeConfigs;
  }

  public int size() {
    return routeConfigs.size();
  }

  public boolean isEmpty() {
    return routeConfigs.isEmpty();
  }

  // --- Internal helpers --------------------------------------------------

  /**
   * Very small, allocation‑light matcher for path templates of the form
   * /api/v1/challenges/{challengeId}/sub/{other}. A template segment enclosed in
   * curly braces matches exactly one concrete path segment (no '/'). No regex
   * evaluation is used for performance & simplicity.
   */
  private static boolean pathPatternMatches(String template, String concrete) {
    // Quick equality check – already handled earlier, but keeps logic robust.
    if (template.equals(concrete)) return true;

    String[] tSegs = template.split("/");
    String[] cSegs = concrete.split("/");
    if (tSegs.length != cSegs.length) return false;
    for (int i = 0; i < tSegs.length; i++) {
      String t = tSegs[i];
      String c = cSegs[i];
      if (t.isEmpty() && c.isEmpty()) continue; // leading slash creates first empty segment
      boolean isVar = t.startsWith("{") && t.endsWith("}") && t.length() > 2;
      if (isVar) continue; // wildcard segment
      if (!t.equals(c)) return false; // literal mismatch
    }
    return true;
  }

  /** Duplicate of RouteKey normalization to avoid widening its API. */
  private static String normalizePath(String raw) {
    String p = raw.trim();
    if (p.isEmpty()) throw new IllegalArgumentException("path must not be blank");
    p = p.replaceAll("/{2,}", "/");
    if (!p.startsWith("/")) p = "/" + p;
    if (p.length() > 1 && p.endsWith("/")) {
      p = p.substring(0, p.length() - 1);
    }
    return p;
  }
}
