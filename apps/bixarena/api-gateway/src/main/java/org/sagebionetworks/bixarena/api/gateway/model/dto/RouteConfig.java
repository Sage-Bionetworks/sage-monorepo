package org.sagebionetworks.bixarena.api.gateway.model.dto;

import java.util.Collection;
import java.util.Set;

/**
 * Configuration for a specific route in the API Gateway.
 *
 * <p>Semantics:
 * - scopes: OAuth2 scopes the resource server expects if the caller is authenticated.
 * - audience: OAuth2 audience/resource identifier (optional).
 * - anonymousAccess: if true, the gateway permits unauthenticated access, but a JWT
 *   with proper scope MAY still be presented and forwarded to the resource server.
 * - rateLimitRequestsPerMinute: Maximum requests per minute for this route (optional, null = use default).
 *
 * Immutable, serialization-friendly, and safe by default.
 */
public record RouteConfig(
    Set<String> scopes,
    String audience,
    boolean anonymousAccess,
    Integer rateLimitRequestsPerMinute) {
  /** Default: no scopes, no audience, anonymous disabled, no rate limit override. */
  public RouteConfig() {
    this(Set.of(), null, false, null);
  }

  /**
   * Canonical constructor with defensive copying and light normalization.
   */
  public RouteConfig {
    scopes = (scopes == null) ? Set.of() : Set.copyOf(scopes);
    audience = (audience != null && !audience.isBlank()) ? audience : null;
    // No invariant that forbids (anonymousAccess == true && !scopes.isEmpty()) by design.
    // rateLimitRequestsPerMinute: null means use default, positive integer means override
    if (rateLimitRequestsPerMinute != null && rateLimitRequestsPerMinute <= 0) {
      throw new IllegalArgumentException("rateLimitRequestsPerMinute must be positive if specified");
    }
  }

  /** True if any scopes are configured. */
  public boolean hasScopes() {
    return !scopes.isEmpty();
  }

  /** True if a non-blank audience is configured. */
  public boolean hasAudience() {
    return audience != null;
  }

  /** True if a custom rate limit is configured for this route. */
  public boolean hasRateLimit() {
    return rateLimitRequestsPerMinute != null;
  }

  // Convenience "withers" since records don't generate them
  public RouteConfig withScopes(Collection<String> newScopes) {
    return new RouteConfig(
      newScopes == null ? Set.of() : Set.copyOf(newScopes),
      audience,
      anonymousAccess,
      rateLimitRequestsPerMinute
    );
  }

  public RouteConfig withAudience(String newAudience) {
    return new RouteConfig(
      scopes,
      (newAudience != null && !newAudience.isBlank()) ? newAudience : null,
      anonymousAccess,
      rateLimitRequestsPerMinute
    );
  }

  public RouteConfig withAnonymousAccess(boolean newAnonymousAccess) {
    return new RouteConfig(scopes, audience, newAnonymousAccess, rateLimitRequestsPerMinute);
  }

  public RouteConfig withRateLimit(Integer requestsPerMinute) {
    return new RouteConfig(scopes, audience, anonymousAccess, requestsPerMinute);
  }
}
