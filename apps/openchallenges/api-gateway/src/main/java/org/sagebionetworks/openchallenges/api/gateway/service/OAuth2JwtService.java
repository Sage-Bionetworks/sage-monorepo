package org.sagebionetworks.openchallenges.api.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service for validating JWT tokens by calling the auth service validation endpoint.
 * 
 * This service validates JWT tokens by making HTTP requests to the auth service
 * instead of doing local validation, which ensures consistency with the auth service's
 * token generation and validation logic.
 */
@Service
public class OAuth2JwtService {

  private static final Logger log = LoggerFactory.getLogger(OAuth2JwtService.class);

  private final WebClient webClient;
  @SuppressWarnings("unused") // Keep for potential future API key validation
  private final String authServiceBaseUrl;
  private final String authServiceOAuth2BaseUrl;

  public OAuth2JwtService(
      @Value("${openchallenges.auth.service-url:http://openchallenges-auth-service:8087/v1}") String authServiceUrl,
      WebClient.Builder webClientBuilder) {
    this.authServiceBaseUrl = authServiceUrl;
    // OAuth2 endpoints are at the root level, not under /v1
    this.authServiceOAuth2BaseUrl = authServiceUrl.replace("/v1", "");
    this.webClient = webClientBuilder.build();
    
    log.info("OAuth2JwtService initialized with auth service URL: {}", authServiceUrl);
  }

  /**
   * Validate JWT token using OAuth2 introspection endpoint.
   * 
   * @param token The JWT token to validate
   * @return Mono<JwtValidationResponse> with validation result
   */
  public Mono<JwtValidationResponse> validateJwt(String token) {
    log.debug("Validating JWT token via OAuth2 introspection endpoint");

    // OAuth2 introspection requires client authentication
    String clientCredentials = java.util.Base64.getEncoder()
        .encodeToString("openchallenges-client:secret".getBytes());

    return webClient
        .post()
        .uri(authServiceOAuth2BaseUrl + "/oauth2/introspect")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Authorization", "Basic " + clientCredentials)
        .bodyValue("token=" + token)
        .retrieve()
        .bodyToMono(IntrospectionResponse.class)
        .map(this::mapToJwtValidationResponse)
        .doOnSuccess(response -> {
          if (response.isValid()) {
            log.debug("JWT validation successful for user: {}", response.getUsername());
          } else {
            log.debug("JWT validation failed: {}", response.getErrorMessage());
          }
        })
        .onErrorResume(ex -> {
          log.error("Error during JWT validation", ex);
          return Mono.just(JwtValidationResponse.invalid("Authentication service unavailable"));
        });
  }

  /**
   * Map OAuth2 introspection response to our internal validation response.
   */
  private JwtValidationResponse mapToJwtValidationResponse(IntrospectionResponse introspection) {
    if (!introspection.isActive()) {
      return JwtValidationResponse.invalid("Token is not active");
    }

    return JwtValidationResponse.builder()
        .valid(true)
        .userId(introspection.getSub())
        .username(introspection.getUsername())
        .role(introspection.getScope()) // could be used for role
        .expiresAt(String.valueOf(introspection.getExp()))
        .build();
  }

  /**
   * OAuth2 introspection response DTO.
   */
  public static class IntrospectionResponse {
    private boolean active;
    private String sub;
    private String username;
    private String scope;
    private long exp;
    private String aud;
    private String iss;

    // Getters and setters
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public long getExp() { return exp; }
    public void setExp(long exp) { this.exp = exp; }
    
    public String getAud() { return aud; }
    public void setAud(String aud) { this.aud = aud; }
    
    public String getIss() { return iss; }
    public void setIss(String iss) { this.iss = iss; }
  }

  /**
   * JWT validation response DTO.
   */
  public static class JwtValidationResponse {
    private boolean valid;
    private String userId;
    private String username;
    private String role;
    private String expiresAt;
    private String errorMessage;

    // Default constructor for JSON deserialization
    public JwtValidationResponse() {}

    public static JwtValidationResponse invalid(String errorMessage) {
      JwtValidationResponse response = new JwtValidationResponse();
      response.valid = false;
      response.errorMessage = errorMessage;
      return response;
    }

    public static JwtValidationResponseBuilder builder() {
      return new JwtValidationResponseBuilder();
    }

    // Getters and setters for JSON serialization/deserialization
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
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    // Builder class
    public static class JwtValidationResponseBuilder {
      private JwtValidationResponse response = new JwtValidationResponse();

      public JwtValidationResponseBuilder valid(boolean valid) {
        response.valid = valid;
        return this;
      }

      public JwtValidationResponseBuilder userId(String userId) {
        response.userId = userId;
        return this;
      }

      public JwtValidationResponseBuilder username(String username) {
        response.username = username;
        return this;
      }

      public JwtValidationResponseBuilder role(String role) {
        response.role = role;
        return this;
      }

      public JwtValidationResponseBuilder expiresAt(String expiresAt) {
        response.expiresAt = expiresAt;
        return this;
      }

      public JwtValidationResponse build() {
        return response;
      }
    }
  }
}