package org.sagebionetworks.openchallenges.auth.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for OpenChallenges Auth Service.
 *
 * All properties are prefixed with 'app.'
 * Properties are validated on application startup.
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
@Validated
public class AuthServiceProperties {

  /**
   * OAuth2 and JWT configuration
   */
  @Valid
  private OAuth2Config oauth2 = new OAuth2Config();

  /**
   * Web authentication configuration
   */
  @Valid
  private WebConfig web = new WebConfig();

  /**
   * API endpoint configuration
   */
  @Valid
  private ApiConfig api = new ApiConfig();

  /**
   * API key configuration
   */
  @Valid
  private ApiKeyConfig apiKey = new ApiKeyConfig();

  @Data
  public static class OAuth2Config {

    /**
     * The issuer URL for JWT tokens and OAuth2 authorization server.
     * Used in JWT 'iss' claim and authorization server settings.
     *
     * Example: http://localhost:8087 or https://auth.openchallenges.io
     */
    @NotBlank(message = "OAuth2 issuer URL must not be blank")
    @Pattern(regexp = "https?://.*", message = "OAuth2 issuer URL must be a valid HTTP/HTTPS URL")
    private String issuerUrl = "http://openchallenges-auth-service:8087";

    /**
     * The JWK Set endpoint URL for JWT token validation.
     * Used by JWT decoder to fetch public keys for signature verification.
     *
     * Example: http://localhost:8087/oauth2/jwks
     */
    @NotBlank(message = "JWK set URL must not be blank")
    @Pattern(regexp = "https?://.*", message = "JWK set URL must be a valid HTTP/HTTPS URL")
    private String jwkSetUrl = "http://localhost:8087/oauth2/jwks";

    /**
     * Default audience for authorization code flow (browser login).
     * Used when no specific resource is requested.
     */
    @NotBlank(message = "Default audience must not be blank")
    private String defaultAudience = "urn:openchallenges:auth-service";

    /**
     * Access token time-to-live in minutes.
     */
    @Min(value = 1, message = "Access token TTL must be at least 1 minute")
    @Max(value = 10080, message = "Access token TTL must not exceed 1 week (10080 minutes)")
    private int accessTokenTtlMinutes = 60;

    /**
     * Refresh token time-to-live in hours.
     */
    @Min(value = 1, message = "Refresh token TTL must be at least 1 hour")
    @Max(value = 8760, message = "Refresh token TTL must not exceed 1 year (8760 hours)")
    private int refreshTokenTtlHours = 24;

    /**
     * Base URL for the auth service (used for redirect URIs when not explicitly configured).
     */
    @NotBlank(message = "Base URL must not be blank")
    @Pattern(regexp = "https?://.*", message = "Base URL must be a valid HTTP/HTTPS URL")
    private String baseUrl = "http://localhost:8087";

    /**
     * Google OAuth2 provider configuration
     */
    @Valid
    private ProviderConfig google = new ProviderConfig();

    /**
     * Synapse OAuth2 provider configuration
     */
    @Valid
    private ProviderConfig synapse = new ProviderConfig();

    @Data
    public static class ProviderConfig {
      /**
       * OAuth2 client ID for the provider
       */
      private String clientId = "";

      /**
       * OAuth2 client secret for the provider
       */
      private String clientSecret = "";

      /**
       * OAuth2 redirect URI for the provider.
       * If not set, will default to baseUrl + /auth/callback
       */
      private String redirectUri = "";
    }
  }

  @Data
  public static class WebConfig {

    /**
     * Cookie name for storing OAuth2 access tokens.
     * Used by the web authentication filter.
     */
    @NotBlank(message = "Access token cookie name must not be blank")
    @Pattern(
      regexp = "[a-zA-Z][a-zA-Z0-9_]*",
      message = "Cookie name must start with a letter and contain only alphanumeric characters and underscores"
    )
    private String accessTokenCookieName = "oc_access_token";
  }

  @Data
  public static class ApiConfig {

    /**
     * API endpoint patterns that are publicly accessible.
     * All other endpoints require authentication.
     */
    @NotEmpty(message = "Public endpoints list must not be empty")
    private String[] publicEndpoints = {
      "/.well-known/**",
      "/actuator/health",
      "/actuator/info",
      "/auth/callback",
      "/auth/oauth2/google",
      "/auth/oauth2/synapse",
      "/error",
      "/login",
      "/logout",
      "/oauth2/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/v3/api-docs",
      "/v3/api-docs/**",
    };
  }

  @Data
  public static class ApiKeyConfig {

    /**
     * The prefix for API keys (e.g., oc_dev_, oc_stage_, oc_prod_)
     */
    @NotBlank(message = "API key prefix must not be blank")
    @Pattern(
      regexp = "^[a-zA-Z][a-zA-Z0-9_]*_$",
      message = "API key prefix must start with a letter, contain only alphanumeric characters and underscores, and end with an underscore"
    )
    private String prefix = "oc_dev_";

    /**
     * The length of the random part of the API key (characters after prefix)
     */
    @Min(value = 20, message = "API key length must be at least 20 characters")
    @Max(value = 128, message = "API key length must not exceed 128 characters")
    private int length = 40;
  }
}
