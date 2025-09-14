package org.sagebionetworks.openchallenges.api.gateway.routing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sagebionetworks.openchallenges.api.gateway.model.RouteConfig;

/**
 * Immutable, thread-safe registry of route configurations.
 *
 * Built at startup (from YAML, DB, or generator output) and injected
 * wherever routing metadata is needed.
 */
public final class RouteConfigRegistry {

  private final Map<RouteKey, RouteConfig> routeConfigs;

  /** Empty registry. */
  public RouteConfigRegistry() {
    this.routeConfigs = Map.of();
  }

  /** Defensive copy constructor. */
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

  /** Lookup route configuration. */
  public Optional<RouteConfig> getRouteConfig(String method, String path) {
    return Optional.ofNullable(routeConfigs.get(RouteKey.of(method, path)));
  }

  /** Required scopes for a route. */
  public Set<String> getScopesForRoute(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::scopes).orElse(Set.of());
  }

  /** Audience for a route, if configured. */
  public Optional<String> getAudienceForRoute(String method, String path) {
    return getRouteConfig(method, path)
      .map(RouteConfig::audience)
      .filter(a -> a != null && !a.isBlank());
  }

  /** Whether anonymous access is allowed. */
  public boolean isAnonymousAccessAllowed(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::anonymousAccess).orElse(false);
  }

  /** All route configs (immutable). */
  public Map<RouteKey, RouteConfig> getAllRouteConfigs() {
    return routeConfigs;
  }

  public int size() {
    return routeConfigs.size();
  }

  public boolean isEmpty() {
    return routeConfigs.isEmpty();
  }
}
