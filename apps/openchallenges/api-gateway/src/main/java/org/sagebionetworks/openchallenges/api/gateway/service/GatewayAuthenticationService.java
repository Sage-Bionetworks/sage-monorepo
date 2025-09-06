package org.sagebionetworks.openchallenges.api.gateway.service;

import java.util.List;
import org.sagebionetworks.openchallenges.api.gateway.configuration.RouteScopeConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.ApiKeyParser.ParsedApiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Service for validating authentication tokens via the Auth Service internal endpoints.
 * Acts as a bridge between the API Gateway and the Auth Service.
 */
@Service
public class GatewayAuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(GatewayAuthenticationService.class);

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
    logger.debug("Validating JWT token via Auth Service");

    JwtValidationRequest request = new JwtValidationRequest(token);

    return authServiceClient
      .post()
      .uri("/auth/jwt/validate")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(JwtValidationResponse.class)
      .doOnNext(response ->
        logger.debug("JWT validation successful for user: {}", response.getUsername())
      )
      .onErrorResume(WebClientResponseException.class, ex -> {
        logger.warn("JWT validation failed: {} - {}", ex.getStatusCode(), ex.getMessage());
        return Mono.error(new AuthenticationException("JWT validation failed", ex));
      })
      .onErrorResume(Exception.class, ex -> {
        logger.error("Error during JWT validation", ex);
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
  public Mono<OAuth2TokenResponse> exchangeApiKeyForJwt(String apiKey, String method, String path) {
    logger.debug("Exchanging API key for JWT using OAuth2 client credentials flow for route: {} {}", method, path);

    // Parse API key to extract client_id and secret
    ParsedApiKey parsedKey = ApiKeyParser.parseApiKey(apiKey);
    String clientId = "oc_api_key_" + parsedKey.getSuffix(); // Use suffix for client_id - matches auth service format
    String clientSecret = parsedKey.getSecret();

    // Get required scopes for this route
    List<String> requiredScopes = routeScopeConfiguration.getScopesForRoute(method, path);
    String scopeParam = requiredScopes.isEmpty() ? "" : "&scope=" + String.join(" ", requiredScopes);
    
    logger.debug("Requesting OAuth2 token for client {} with scopes: {}", clientId, requiredScopes);

    return oauth2ServiceClient
      .post()
      .uri("/oauth2/token")
      .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
      .header("Content-Type", "application/x-www-form-urlencoded")
      .bodyValue("grant_type=client_credentials" + scopeParam)
      .retrieve()
      .bodyToMono(OAuth2TokenResponse.class)
      .doOnNext(response ->
        logger.debug("OAuth2 token exchange successful for client: {} with scopes: {}", clientId, requiredScopes)
      )
      .onErrorResume(WebClientResponseException.class, ex -> {
        logger.warn(
          "OAuth2 token exchange failed for client {}: {} - {}",
          clientId,
          ex.getStatusCode(),
          ex.getMessage()
        );
        return Mono.error(new AuthenticationException("API key authentication failed", ex));
      })
      .onErrorResume(Exception.class, ex -> {
        logger.error("Error during OAuth2 token exchange for client: {}", clientId, ex);
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
  public Mono<OAuth2TokenResponse> exchangeApiKeyForJwt(String apiKey) {
    return exchangeApiKeyForJwt(apiKey, "", "");
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
