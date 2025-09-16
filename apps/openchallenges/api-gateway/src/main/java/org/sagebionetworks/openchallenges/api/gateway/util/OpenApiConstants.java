package org.sagebionetworks.openchallenges.api.gateway.util;

/**
 * Constants for OpenAPI specification extensions and related configuration.
 */
public final class OpenApiConstants {

  /**
   * OpenAPI extension for specifying OAuth2 audience/resource identifier.
   * Used to determine the 'resource' parameter in OAuth2 token requests (RFC 8707).
   */
  public static final String OAUTH2_AUDIENCE_EXTENSION = "x-oauth2-audience";

  /**
   * OpenAPI extension for marking endpoints as allowing anonymous access.
   * Used to determine if a route can be accessed without authentication.
   */
  public static final String ANONYMOUS_ACCESS_EXTENSION = "x-anonymous-access";

  private OpenApiConstants() {
    // Utility class - prevent instantiation
  }
}
