package org.sagebionetworks.openchallenges.api.gateway.observability;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteConfig;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteConfigRegistry;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteKey;

/**
 * Utility to render RouteConfigRegistry for logging.
 * - INFO: single-line JSON (Grafana/Loki-friendly)
 * - DEBUG: pretty multi-line text (human-friendly)
 */
public final class RouteConfigPrinter {

  private static final ObjectMapper JSON = new ObjectMapper()
    .findAndRegisterModules()
    .disable(SerializationFeature.INDENT_OUTPUT); // compact, single-line

  private RouteConfigPrinter() {}

  /** One-line human-readable summary (safe for any sink). */
  public static String summary(RouteConfigRegistry registry) {
    Map<String, Long> byMethod = countByMethod(registry);
    String methods = byMethod
      .entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .map(e -> e.getKey() + "=" + e.getValue())
      .collect(Collectors.joining(", "));
    return "Route registry: %d routes [%s]".formatted(registry.size(), methods);
  }

  /** Multi-line pretty dump for DEBUG-only logs. */
  public static String detailed(RouteConfigRegistry registry) {
    StringBuilder sb = new StringBuilder();
    sb.append("Route registry dump (").append(registry.size()).append(" routes):\n");
    registry
      .getAllRouteConfigs()
      .forEach((key, cfg) -> {
        sb.append("  • ").append(key.method()).append(" ").append(key.path()).append("\n");
        sb.append("      scopes: ").append(formatScopes(cfg.scopes())).append("\n");
        sb.append("      audience: ").append(nullToDash(cfg.audience())).append("\n");
        sb.append("      anonymousAccess: ").append(cfg.anonymousAccess()).append("\n");
      });
    return sb.toString();
  }

  /** Single-line JSON summary (best for INFO in Grafana/Loki). */
  public static String jsonSummary(RouteConfigRegistry registry) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("event", "gateway.routeRegistry.summary");
    payload.put("ts", Instant.now().toString());
    payload.put("count", registry.size());
    payload.put("byMethod", countByMethod(registry));
    return toJson(payload);
  }

  /**
   * NDJSON-style: one compact JSON object per route (each is a single line).
   * Useful if you want to log individual routes at INFO for easier querying.
   */
  public static List<String> jsonPerRoute(RouteConfigRegistry registry) {
    return registry
      .getAllRouteConfigs()
      .entrySet()
      .stream()
      .map(e -> toJson(routeEvent(e.getKey(), e.getValue())))
      .toList();
  }

  // -------- helpers

  private static Map<String, Long> countByMethod(RouteConfigRegistry registry) {
    return registry
      .getAllRouteConfigs()
      .keySet()
      .stream()
      .collect(Collectors.groupingBy(RouteKey::method, Collectors.counting()));
  }

  private static Map<String, Object> routeEvent(RouteKey key, RouteConfig cfg) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("event", "gateway.routeRegistry.route");
    m.put("ts", Instant.now().toString());
    m.put("method", key.method());
    m.put("path", key.path());
    m.put("audience", cfg.audience()); // redact or omit if sensitive
    m.put("anonymousAccess", cfg.anonymousAccess());
    m.put("scopes", cfg.scopes()); // consider redacting if sensitive
    return m;
  }

  private static String toJson(Object o) {
    try {
      return JSON.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      // Fallback to toString() so we never break logging
      return String.valueOf(o);
    }
  }

  private static String formatScopes(Set<String> scopes) {
    return (scopes == null || scopes.isEmpty()) ? "-" : scopes.toString();
  }

  private static String nullToDash(String s) {
    return (s == null || s.isBlank()) ? "-" : s;
  }
}
