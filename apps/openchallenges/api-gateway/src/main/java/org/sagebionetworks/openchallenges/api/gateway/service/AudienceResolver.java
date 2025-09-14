package org.sagebionetworks.openchallenges.api.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.model.config.RouteConfigRegistry;
import org.sagebionetworks.openchallenges.api.gateway.util.OpenApiConstants;
import org.springframework.stereotype.Service;

/**
 * Service for resolving OAuth2 audience identifiers based on route paths.
 * Uses route configuration to determine the appropriate resource identifier
 * for RFC 8707 resource parameter in OAuth2 token requests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudienceResolver {

  private final RouteConfigRegistry routeConfigRegistry;

  /**
   * Get the resource identifier for a specific route.
   * Used as the 'resource' parameter in OAuth2 token requests (RFC 8707).
   *
   * @param method HTTP method
   * @param path Request path
   * @return Resource identifier if configured in OpenAPI spec, null otherwise
   */
  public String getResourceIdentifierForRoute(String method, String path) {
    log.debug("Resolving audience for route: {} {}", method, path);

    return routeConfigRegistry
      .getAudienceForRoute(method, path)
      .orElseGet(() -> {
        log.warn(
          "No audience configured for route {} {}. " +
          "Consider adding {} to the OpenAPI specification.",
          method,
          path,
          OpenApiConstants.OAUTH2_AUDIENCE_EXTENSION
        );
        return null;
      });
  }
}
