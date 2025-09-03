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
  private final String authServiceBaseUrl;

  public OAuth2JwtService(
      @Value("${openchallenges.auth.service-url:http://openchallenges-auth-service:8087/v1}") String authServiceUrl,
      WebClient.Builder webClientBuilder) {
    this.authServiceBaseUrl = authServiceUrl;
    this.webClient = webClientBuilder.build();
    
    log.info("OAuth2JwtService initialized with auth service URL: {}", authServiceUrl);
  }

  /**
   * Validate JWT token by calling the auth service validation endpoint.
   * 
   * @param token The JWT token to validate
   * @return Mono<JwtValidationResponse> with validation result
   */
  public Mono<JwtValidationResponse> validateJwt(String token) {
    log.debug("Validating JWT token via auth service");

    return webClient
        .post()
        .uri(authServiceBaseUrl + "/auth/tokens/validate")
        .header("Authorization", "Bearer " + token)
        .retrieve()
        .bodyToMono(JwtValidationResponse.class)
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