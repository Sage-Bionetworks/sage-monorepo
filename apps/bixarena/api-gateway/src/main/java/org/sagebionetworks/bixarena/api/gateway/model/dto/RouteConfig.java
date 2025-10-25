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
 *
 * Immutable, serialization-friendly, and safe by default.
 */
public record RouteConfig(Set<String> scopes, String audience, boolean anonymousAccess) {
  /** Default: no scopes, no audience, anonymous disabled. */
  public RouteConfig() {
    this(Set.of(), null, false);
  }

  /**
   * Canonical constructor with defensive copying and light normalization.
   */
  public RouteConfig {
    scopes = (scopes == null) ? Set.of() : Set.copyOf(scopes);
    audience = (audience != null && !audience.isBlank()) ? audience : null;
    // No invariant that forbids (anonymousAccess == true && !scopes.isEmpty()) by design.
  }

  /** True if any scopes are configured. */
  public boolean hasScopes() {
    return !scopes.isEmpty();
  }

  /** True if a non-blank audience is configured. */
  public boolean hasAudience() {
    return audience != null;
  }

  // Convenience "withers" since records don’t generate them
  public RouteConfig withScopes(Collection<String> newScopes) {
    return new RouteConfig(
      newScopes == null ? Set.of() : Set.copyOf(newScopes),
      audience,
      anonymousAccess
    );
  }

  public RouteConfig withAudience(String newAudience) {
    return new RouteConfig(
      scopes,
      (newAudience != null && !newAudience.isBlank()) ? newAudience : null,
      anonymousAccess
    );
  }

  public RouteConfig withAnonymousAccess(boolean newAnonymousAccess) {
    return new RouteConfig(scopes, audience, newAnonymousAccess);
  }
}
