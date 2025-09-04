package org.sagebionetworks.openchallenges.api.gateway.service;

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

  public GatewayAuthenticationService(
      @Value("${openchallenges.auth.service-url:http://openchallenges-auth-service:8080/v1}") String authServiceUrl) {
    this.authServiceClient = WebClient.builder()
        .baseUrl(authServiceUrl)
        .build();
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
            logger.debug("JWT validation successful for user: {}", response.getUsername()))
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
   * Validates an API key by calling the Auth Service internal endpoint.
   *
   * @param apiKey The API key to validate
   * @return A Mono containing the validation response
   */
  public Mono<ApiKeyValidationResponse> validateApiKey(String apiKey) {
    logger.debug("Validating API key via Auth Service");
    
    ApiKeyValidationRequest request = new ApiKeyValidationRequest(apiKey);
    
    return authServiceClient
        .post()
        .uri("/auth/api-keys/validate")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(ApiKeyValidationResponse.class)
        .doOnNext(response -> 
            logger.debug("API key validation successful for user: {}", response.getUsername()))
        .onErrorResume(WebClientResponseException.class, ex -> {
          logger.warn("API key validation failed: {} - {}", ex.getStatusCode(), ex.getMessage());
          return Mono.error(new AuthenticationException("API key validation failed", ex));
        })
        .onErrorResume(Exception.class, ex -> {
          logger.error("Error during API key validation", ex);
          return Mono.error(new AuthenticationException("Authentication service unavailable", ex));
        });
  }

  /**
   * Exchange an API key for a JWT token using OAuth2 client credentials flow.
   * Treats the API key as a service principal that can get short-lived access tokens.
   *
   * @param apiKey The API key to exchange (format: oc_ak_<prefix>.<secret>)
   * @return A Mono containing the OAuth2 token response
   */
  public Mono<OAuth2TokenResponse> exchangeApiKeyForJwt(String apiKey) {
    logger.debug("Exchanging API key for JWT using OAuth2 client credentials flow");
    
    // Parse API key to extract client_id and secret
    ApiKeyParser.ParsedApiKey parsedKey = ApiKeyParser.parseApiKey(apiKey);
    String clientId = "oc-ak_" + parsedKey.getSuffix(); // Use suffix for client_id
    String clientSecret = parsedKey.getSecret();
    
    // Prepare OAuth2 client credentials request
    OAuth2TokenRequest tokenRequest = new OAuth2TokenRequest(
        "client_credentials",
        null // no specific scope requested - use client's allowed scopes
    );
    
    return authServiceClient
        .post()
        .uri("/oauth2/token")
        .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .bodyValue("grant_type=client_credentials")
        .retrieve()
        .bodyToMono(OAuth2TokenResponse.class)
        .doOnNext(response -> 
            logger.debug("OAuth2 token exchange successful for client: {}", clientId))
        .onErrorResume(WebClientResponseException.class, ex -> {
          logger.warn("OAuth2 token exchange failed for client {}: {} - {}", clientId, ex.getStatusCode(), ex.getMessage());
          return Mono.error(new AuthenticationException("API key authentication failed", ex));
        })
        .onErrorResume(Exception.class, ex -> {
          logger.error("Error during OAuth2 token exchange for client: {}", clientId, ex);
          return Mono.error(new AuthenticationException("Authentication service unavailable", ex));
        });
  }

  // Request/Response DTOs for internal communication

  public static class JwtValidationRequest {
    private String token;

    public JwtValidationRequest() {}
    public JwtValidationRequest(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
  }

  public static class JwtValidationResponse {
    private boolean valid;
    private String userId;
    private String username;
    private String role;
    private String expiresAt;

    public JwtValidationResponse() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
  }

  public static class ApiKeyValidationRequest {
    private String apiKey;

    public ApiKeyValidationRequest() {}
    public ApiKeyValidationRequest(String apiKey) { this.apiKey = apiKey; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
  }

  public static class ApiKeyValidationResponse {
    private boolean valid;
    private String userId;
    private String username;
    private String role;
    private String[] scopes;

    public ApiKeyValidationResponse() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String[] getScopes() { return scopes; }
    public void setScopes(String[] scopes) { this.scopes = scopes; }

    // Helper method to determine if this is a service account
    public String getType() {
      return "service".equals(role) ? "service" : "user";
    }

    // Helper method for service name (could be enhanced later)
    public String getServiceName() {
      return "service".equals(role) ? username : null;
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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String[] getScopes() { return scopes; }
    public void setScopes(String[] scopes) { this.scopes = scopes; }
  }

  public static class ApiKeyJwtExchangeResponse {
    private String jwtToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    public ApiKeyJwtExchangeResponse() {}

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
  }

  public static class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
