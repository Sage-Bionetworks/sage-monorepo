package org.sagebionetworks.openchallenges.api.gateway.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.configuration.RouteScopeConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.OAuth2TokenResponseDto;
import org.sagebionetworks.openchallenges.api.gateway.service.ApiKeyParser.ParsedApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Service for validating authentication tokens via the Auth Service internal endpoints.
 * Acts as a bridge between the API Gateway and the Auth Service.
 */
@Slf4j
@Service
public class GatewayAuthenticationService {

  private final WebClient authServiceClient;
  private final WebClient oauth2ServiceClient;
  private final RouteScopeConfiguration routeScopeConfiguration;

  public GatewayAuthenticationService(
    @Value(
      "${openchallenges.auth.service-url:http://openchallenges-auth-service:8080/v1}"
    ) String authServiceUrl,
    RouteScopeConfiguration routeScopeConfiguration
  ) {
    this.authServiceClient = WebClient.builder().baseUrl(authServiceUrl).build();

    // OAuth2 endpoints are at the root level, not under /v1
    String oauth2ServiceUrl = authServiceUrl.replace("/v1", "");
    this.oauth2ServiceClient = WebClient.builder().baseUrl(oauth2ServiceUrl).build();

    this.routeScopeConfiguration = routeScopeConfiguration;
  }

  /**
   * Validates a JWT token by calling the Auth Service internal endpoint.
   *
   * @param token The JWT token to validate
   * @return A Mono containing the validation response
   */
  public Mono<JwtValidationResponse> validateJwt(String token) {
    log.debug("Validating JWT token via Auth Service");

    JwtValidationRequest request = new JwtValidationRequest(token);

    return authServiceClient
      .post()
      .uri("/auth/jwt/validate")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(JwtValidationResponse.class)
      .doOnNext(response ->
        log.debug("JWT validation successful for user: {}", response.getUsername())
      )
      .onErrorResume(WebClientResponseException.class, ex -> {
        log.warn("JWT validation failed: {} - {}", ex.getStatusCode(), ex.getMessage());
        return Mono.error(new AuthenticationException("JWT validation failed", ex));
      })
      .onErrorResume(Exception.class, ex -> {
        log.error("Error during JWT validation", ex);
        return Mono.error(new AuthenticationException("Authentication service unavailable", ex));
      });
  }

  /**
   * Exchanges an API key for a JWT using OAuth2 client credentials flow.
   * The scopes requested are determined by the route being accessed.
   *
   * @param apiKey The API key to exchange (format: oc_ak_<prefix>.<secret>)
   * @param method HTTP method (GET, POST, etc.)
   * @param path   URL path
   * @return A Mono containing the OAuth2 token response
   */
  public Mono<OAuth2TokenResponseDto> exchangeApiKeyForJwt(
    String apiKey,
    String method,
    String path
  ) {
    log.info("=== API GATEWAY: Starting API key exchange for JWT ===");
    log.info("Route: {} {}", method, path);

    // Parse API key to extract client_id and secret
    ParsedApiKey parsedKey;
    try {
      parsedKey = ApiKeyParser.parseApiKey(apiKey);
      log.info(
        "Parsed API key - Environment: {}, Suffix: {}, Secret present: {}",
        parsedKey.getEnvironmentPrefix(),
        parsedKey.getSuffix(),
        parsedKey.getSecret() != null && !parsedKey.getSecret().isEmpty()
      );
    } catch (Exception e) {
      log.error("Failed to parse API key: {}", e.getMessage());
      throw e;
    }

    String clientId = "oc_api_key_" + parsedKey.getSuffix(); // Use suffix for client_id - matches auth service format
    String clientSecret = parsedKey.getSecret();
    log.info("OAuth2 Client ID: {}", clientId);

    // Get required scopes for this route
    List<String> requiredScopes = routeScopeConfiguration.getScopesForRoute(method, path);
    String scopeParam = requiredScopes.isEmpty()
      ? ""
      : "&scope=" + String.join(" ", requiredScopes);

    // Get RFC 8707 resource identifier for audience/resource parameter
    String resourceIdentifier = routeScopeConfiguration.getResourceIdentifierForRoute(method, path);
    String resourceParam = resourceIdentifier != null ? "&resource=" + resourceIdentifier : "";

    log.info("Required scopes for route: {}", requiredScopes);
    log.info("RFC 8707 resource identifier for route: {}", resourceIdentifier);
    log.info("OAuth2 request URL: /oauth2/token");
    log.info("OAuth2 request body: grant_type=client_credentials{}{}", scopeParam, resourceParam);

    return oauth2ServiceClient
      .post()
      .uri("/oauth2/token")
      .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
      .header("Content-Type", "application/x-www-form-urlencoded")
      .bodyValue("grant_type=client_credentials" + scopeParam + resourceParam)
      .retrieve()
      .bodyToMono(OAuth2TokenResponseDto.class)
      .doOnNext(response -> {
        log.info("=== API GATEWAY: OAuth2 token exchange SUCCESSFUL ===");
        log.info(
          "Client: {}, Scopes: {}, Resource: {}",
          clientId,
          requiredScopes,
          resourceIdentifier
        );
        log.info(
          "Access token received (length: {})",
          response.getAccessToken() != null ? response.getAccessToken().length() : 0
        );
      })
      .onErrorResume(WebClientResponseException.class, ex -> {
        log.error("=== API GATEWAY: OAuth2 token exchange FAILED ===");
        log.error(
          "Client: {}, Status: {}, Response: {}",
          clientId,
          ex.getStatusCode(),
          ex.getResponseBodyAsString()
        );
        return Mono.error(new AuthenticationException("API key authentication failed", ex));
      })
      .onErrorResume(Exception.class, ex -> {
        log.error("=== API GATEWAY: OAuth2 token exchange ERROR ===");
        log.error("Client: {}, Error: {}", clientId, ex.getMessage(), ex);
        return Mono.error(new AuthenticationException("Authentication service unavailable", ex));
      });
  }

  /**
   * Exchanges an API key for a JWT using OAuth2 client credentials flow without specific route scopes.
   * This method is kept for backward compatibility.
   *
   * @param apiKey The API key to exchange (format: oc_ak_<prefix>.<secret>)
   * @return A Mono containing the OAuth2 token response
   */
  public Mono<OAuth2TokenResponseDto> exchangeApiKeyForJwt(String apiKey) {
    return exchangeApiKeyForJwt(apiKey, "", "");
  }

  /**
   * Generates an anonymous JWT token for public endpoints using a pre-configured anonymous client.
   *
   * @param method HTTP method
   * @param path URL path
   * @return A Mono containing the OAuth2 token response
   */
  public Mono<OAuth2TokenResponseDto> generateAnonymousJwt(String method, String path) {
    log.debug("Generating anonymous JWT for route: {} {}", method, path);

    // Use a predefined anonymous client for public access
    String anonymousClientId = "oc_anonymous_client";
    String anonymousClientSecret = "anonymous_secret_for_public_access";

    // Get required scopes for this route
    List<String> requiredScopes = routeScopeConfiguration.getScopesForRoute(method, path);
    String scopeParam = requiredScopes.isEmpty()
      ? ""
      : "&scope=" + String.join(" ", requiredScopes);

    log.debug("Requesting anonymous OAuth2 token with scopes: {}", requiredScopes);

    return oauth2ServiceClient
      .post()
      .uri("/oauth2/token")
      .headers(headers -> headers.setBasicAuth(anonymousClientId, anonymousClientSecret))
      .header("Content-Type", "application/x-www-form-urlencoded")
      .bodyValue("grant_type=client_credentials" + scopeParam)
      .retrieve()
      .bodyToMono(OAuth2TokenResponseDto.class)
      .doOnNext(response ->
        log.debug("Anonymous OAuth2 token generated successfully with scopes: {}", requiredScopes)
      )
      .onErrorResume(WebClientResponseException.class, ex -> {
        log.warn(
          "Anonymous OAuth2 token generation failed: {} - {}",
          ex.getStatusCode(),
          ex.getMessage()
        );
        return Mono.error(new AuthenticationException("Anonymous token generation failed", ex));
      })
      .onErrorResume(Exception.class, ex -> {
        log.error("Error during anonymous OAuth2 token generation", ex);
        return Mono.error(new AuthenticationException("Authentication service unavailable", ex));
      });
  }

  // Request/Response DTOs for internal communication

  public static class JwtValidationRequest {

    private String token;

    public JwtValidationRequest() {}

    public JwtValidationRequest(String token) {
      this.token = token;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }

  public static class JwtValidationResponse {

    private boolean valid;
    private String userId;
    private String username;
    private String role;
    private String expiresAt;

    public JwtValidationResponse() {}

    public boolean isValid() {
      return valid;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getExpiresAt() {
      return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
      this.expiresAt = expiresAt;
    }
  }

  public static class ApiKeyJwtExchangeRequest {

    private String userId;
    private String username;
    private String role;
    private String[] scopes;

    public ApiKeyJwtExchangeRequest() {}

    public ApiKeyJwtExchangeRequest(String userId, String username, String role, String[] scopes) {
      this.userId = userId;
      this.username = username;
      this.role = role;
      this.scopes = scopes;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String[] getScopes() {
      return scopes;
    }

    public void setScopes(String[] scopes) {
      this.scopes = scopes;
    }
  }

  public static class ApiKeyJwtExchangeResponse {

    private String jwtToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    public ApiKeyJwtExchangeResponse() {}

    public String getJwtToken() {
      return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
      this.jwtToken = jwtToken;
    }

    public String getTokenType() {
      return tokenType;
    }

    public void setTokenType(String tokenType) {
      this.tokenType = tokenType;
    }

    public long getExpiresIn() {
      return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
      this.expiresIn = expiresIn;
    }
  }

  public static class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
