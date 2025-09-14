package org.sagebionetworks.openchallenges.api.gateway.model.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Container for all route configurations loaded from the YAML configuration file.
 * Provides methods to lookup route configurations and audience mappings.
 */
public class RouteConfigRegistry {

  private Map<String, RouteConfig> routeConfigs = new LinkedHashMap<>();

  public RouteConfigRegistry() {}

  public RouteConfigRegistry(Map<String, RouteConfig> routeConfigs) {
    this.routeConfigs = routeConfigs != null
      ? new LinkedHashMap<>(routeConfigs)
      : new LinkedHashMap<>();
  }

  /**
   * Get route configuration for a specific HTTP method and path.
   */
  public Optional<RouteConfig> getRouteConfig(String method, String path) {
    String routeKey = method.toUpperCase() + " " + path;
    return Optional.ofNullable(routeConfigs.get(routeKey));
  }

  /**
   * Get required scopes for a route.
   */
  public List<String> getScopesForRoute(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::getScopes).orElse(List.of());
  }

  /**
   * Get audience identifier for a route.
   */
  public Optional<String> getAudienceForRoute(String method, String path) {
    return getRouteConfig(method, path)
      .map(RouteConfig::getAudience)
      .filter(audience -> audience != null && !audience.trim().isEmpty());
  }

  /**
   * Check if a route allows anonymous access.
   */
  public boolean isAnonymousAccessAllowed(String method, String path) {
    return getRouteConfig(method, path).map(RouteConfig::isAnonymousAccess).orElse(false);
  }

  /**
   * Get all route configurations.
   */
  public Map<String, RouteConfig> getAllRouteConfigs() {
    return new LinkedHashMap<>(routeConfigs);
  }

  /**
   * Add or update a route configuration.
   */
  public void putRouteConfig(String routeKey, RouteConfig config) {
    routeConfigs.put(routeKey, config);
  }

  /**
   * Get the number of configured routes.
   */
  public int size() {
    return routeConfigs.size();
  }

  /**
   * Check if the registry is empty.
   */
  public boolean isEmpty() {
    return routeConfigs.isEmpty();
  }
}
