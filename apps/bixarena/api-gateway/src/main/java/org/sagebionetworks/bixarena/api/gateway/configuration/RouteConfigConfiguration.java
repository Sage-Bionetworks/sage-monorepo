package org.sagebionetworks.bixarena.api.gateway.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import org.sagebionetworks.bixarena.api.gateway.model.dto.RouteConfig;
import org.sagebionetworks.bixarena.api.gateway.model.dto.RouteSpec;
import org.sagebionetworks.bixarena.api.gateway.routing.RouteConfigRegistry;
import org.sagebionetworks.bixarena.api.gateway.routing.RouteKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RouteConfigProperties.class)
public class RouteConfigConfiguration {

  @Bean
  RouteConfigRegistry routeConfigRegistry(RouteConfigProperties props) {
    Map<RouteKey, RouteConfig> typed = new LinkedHashMap<>();

    // Load routes from OpenAPI-generated YAML
    for (RouteSpec r : props.routes()) {
      RouteKey key = RouteKey.of(r.method(), r.path());
      RouteConfig cfg = new RouteConfig(
          r.scopes(),
          r.audience(),
          r.anonymousAccess(),
          r.rateLimitRequestsPerMinute()
      );

      if (typed.putIfAbsent(key, cfg) != null) {
        throw new IllegalArgumentException(
          "Duplicate route: %s %s".formatted(key.method(), key.path())
        );
      }
    }

    // Register infrastructure endpoints programmatically (not in OpenAPI spec)
    // These endpoints allow anonymous access and don't require JWT minting
    registerInfrastructureRoute(typed, "GET", "/actuator/health", 100);
    registerInfrastructureRoute(typed, "GET", "/actuator/metrics", 100);
    registerInfrastructureRoute(typed, "GET", "/favicon.ico", 100);

    return new RouteConfigRegistry(typed);
  }

  /**
   * Helper to register infrastructure routes that aren't part of the OpenAPI spec.
   * These routes allow anonymous access and don't need an audience (no JWT minting).
   */
  private void registerInfrastructureRoute(
      Map<RouteKey, RouteConfig> routes,
      String method,
      String path,
      int rateLimitRequestsPerMinute) {
    RouteKey key = RouteKey.of(method, path);
    RouteConfig cfg = new RouteConfig(
        java.util.Set.of(),    // No scopes
        null,                   // No audience (no JWT minting needed)
        true,                   // Anonymous access allowed
        rateLimitRequestsPerMinute
    );
    routes.put(key, cfg);
  }
}
