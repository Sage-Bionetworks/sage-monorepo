package org.sagebionetworks.openchallenges.api.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Service for validating OAuth2 JWT tokens using both Spring Security's OAuth2 support
 * and HMAC validation for tokens signed with shared secrets.
 * 
 * This service handles two types of JWT tokens:
 * 1. RSA-signed tokens from OAuth2 Authorization Server (validated via JWK Set)
 * 2. HMAC-signed tokens from custom JWT service (validated via shared secret)
 */
@Service
public class OAuth2JwtService {

  private static final Logger log = LoggerFactory.getLogger(OAuth2JwtService.class);

  private final JwtDecoder jwtDecoder;
  private final SecretKey hmacSigningKey;
  private final String authServiceBaseUrl;

  public OAuth2JwtService(
      @Value("${openchallenges.auth.service-url:http://openchallenges-auth-service:8087/v1}") String authServiceUrl,
      @Value("${app.security.jwt.secret-key:openchallenges-default-jwt-secret-key-change-in-production}") String jwtSecretKey) {
    // Remove /v1 suffix since OAuth2 endpoints are at root level
    this.authServiceBaseUrl = authServiceUrl.replace("/v1", "");
    
    // Configure JWT decoder to use the auth service's JWK Set endpoint for RSA tokens
    String jwkSetUri = this.authServiceBaseUrl + "/oauth2/jwks";
    this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    
    // Configure HMAC key for custom JWT tokens (same key as auth service)
    this.hmacSigningKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    
    log.info("OAuth2JwtService initialized with JWK Set URI: {} and HMAC validation support", jwkSetUri);
  }

  /**
   * Validate JWT token using Spring Security's OAuth2 JWT decoder and fallback to HMAC validation.
   *
   * @param token The JWT token to validate
   * @return A Mono containing the validation response
   */
  public Mono<JwtValidationResponse> validateJwt(String token) {
    log.debug("Validating JWT token using OAuth2 and HMAC validation");
    
    if (token == null || token.trim().isEmpty()) {
      return Mono.just(JwtValidationResponse.invalid("Token is null or empty"));
    }

    return Mono.fromCallable(() -> {
      try {
        // First try OAuth2 RSA validation
        Jwt jwt = jwtDecoder.decode(token);
        
        // Extract claims from OAuth2 JWT
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("username");
        String role = jwt.getClaimAsString("role");
        Instant expiresAt = jwt.getExpiresAt();
        
        // Check if token is expired
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
          return JwtValidationResponse.invalid("Token is expired");
        }
        
        JwtValidationResponse response = JwtValidationResponse.builder()
            .valid(true)
            .userId(userId)
            .username(username)
            .role(role)
            .expiresAt(expiresAt != null ? expiresAt.toString() : null)
            .build();
        
        log.debug("OAuth2 JWT validation successful for user: {}", username);
        return response;
        
      } catch (Exception oauthException) {
        log.debug("OAuth2 JWT validation failed, trying HMAC validation: {}", oauthException.getMessage());
        
        try {
          // Fallback to HMAC validation
          Claims claims = Jwts.parser()
              .verifyWith(hmacSigningKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();
          
          String userId = claims.getSubject();
          String username = claims.get("username", String.class);
          String role = claims.get("role", String.class);
          Instant expiresAt = claims.getExpiration() != null ? claims.getExpiration().toInstant() : null;
          
          // Check if token is expired
          if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            return JwtValidationResponse.invalid("Token is expired");
          }
          
          JwtValidationResponse response = JwtValidationResponse.builder()
              .valid(true)
              .userId(userId)
              .username(username)
              .role(role)
              .expiresAt(expiresAt != null ? expiresAt.toString() : null)
              .build();
          
          log.debug("HMAC JWT validation successful for user: {}", username);
          return response;
          
        } catch (Exception hmacException) {
          log.warn("Both OAuth2 and HMAC JWT validation failed. OAuth2: {}, HMAC: {}", 
                   oauthException.getMessage(), hmacException.getMessage());
          return JwtValidationResponse.invalid("Token validation failed");
        }
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

    public static JwtValidationResponse invalid(String errorMessage) {
      JwtValidationResponse response = new JwtValidationResponse();
      response.valid = false;
      response.errorMessage = errorMessage;
      return response;
    }

    public static JwtValidationResponseBuilder builder() {
      return new JwtValidationResponseBuilder();
    }

    // Getters
    public boolean isValid() { return valid; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getExpiresAt() { return expiresAt; }
    public String getErrorMessage() { return errorMessage; }

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
