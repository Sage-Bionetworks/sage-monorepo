package org.sagebionetworks.openchallenges.auth.service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for OpenChallenges Auth Service.
 *
 * All properties are prefixed with 'openchallenges.auth-service.'
 */
@Component
@ConfigurationProperties(prefix = "openchallenges.auth-service")
@Data
public class AuthServiceProperties {

  /**
   * OAuth2 and JWT configuration
   */
  private OAuth2Config oauth2 = new OAuth2Config();

  /**
   * Web authentication configuration
   */
  private WebConfig web = new WebConfig();

  /**
   * API endpoint configuration
   */
  private ApiConfig api = new ApiConfig();

  /**
   * API key configuration
   */
  private ApiKeyConfig apiKey = new ApiKeyConfig();

  @Data
  public static class OAuth2Config {

    /**
     * The issuer URL for JWT tokens and OAuth2 authorization server.
     * Used in JWT 'iss' claim and authorization server settings.
     *
     * Example: http://localhost:8087 or https://auth.openchallenges.io
     */
    private String issuerUrl = "http://openchallenges-auth-service:8087";

    /**
     * The JWK Set endpoint URL for JWT token validation.
     * Used by JWT decoder to fetch public keys for signature verification.
     *
     * Example: http://localhost:8087/oauth2/jwks
     */
    private String jwkSetUrl = "http://localhost:8087/oauth2/jwks";

    /**
     * Default audience for authorization code flow (browser login).
     * Used when no specific resource is requested.
     */
    private String defaultAudience = "urn:openchallenges:auth-service";

    /**
     * Access token time-to-live in minutes.
     */
    private int accessTokenTtlMinutes = 60;

    /**
     * Refresh token time-to-live in hours.
     */
    private int refreshTokenTtlHours = 24;
  }

  @Data
  public static class WebConfig {

    /**
     * Cookie name for storing OAuth2 access tokens.
     * Used by the web authentication filter.
     */
    private String accessTokenCookieName = "oc_access_token";
  }

  @Data
  public static class ApiConfig {

    /**
     * API endpoint patterns that require authentication.
     */
    private String[] protectedEndpoints = { "/v1/**" };

    /**
     * API endpoint patterns that are publicly accessible.
     */
    private String[] publicEndpoints = {
      "/oauth2/**",
      "/.well-known/**",
      "/actuator/health",
      "/actuator/info",
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-ui.html",
      "/login",
      "/auth/oauth2/google",
      "/auth/callback",
      "/logout",
      "/logout/**",
      "/error",
    };
  }

  @Data
  public static class ApiKeyConfig {

    /**
     * The prefix for API keys (e.g., oc_dev_, oc_stage_, oc_prod_)
     */
    private String prefix = "oc_dev_";

    /**
     * The length of the random part of the API key (characters after prefix)
     */
    private int length = 40;
  }
}
