package org.sagebionetworks.openchallenges.challenge.service.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.configuration.AppProperties;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Service for performing OAuth 2.0 Token Exchange (RFC 8693) to obtain tokens
 * for service-to-service communication on behalf of users.
 *
 * This implements the "Token Exchange" grant type to convert incoming user JWTs
 * into audience-specific tokens for calling other services with minimal scopes.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenExchangeService {

  private final RestClient restClient;
  private final AppProperties appProperties;

  /**
   * Exchange the current user's JWT token for a new token scoped for the target service.
   *
   * @param targetAudience The audience (service) the new token should be valid for
   * @param requiredScopes The minimal scopes needed for the target service
   * @return The new JWT token
   */
  public String exchangeTokenForService(String targetAudience, String requiredScopes) {
    log.debug(
      "Exchanging token for target audience: {} with scopes: {}",
      targetAudience,
      requiredScopes
    );

    // For now, use client_credentials flow instead of token exchange
    // This is a simplified approach until token exchange is properly supported
    String requestBody = String.format("grant_type=client_credentials&scope=%s", requiredScopes);

    try {
      String authServiceUrl = appProperties.authService().baseUrl();
      var oauthClient = appProperties.oauth2().client();

      TokenResponse response = restClient
        .post()
        .uri(authServiceUrl + "/oauth2/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .header(
          "Authorization",
          "Basic " + createBasicAuthHeader(oauthClient.clientId(), oauthClient.clientSecret())
        )
        .body(requestBody)
        .retrieve()
        .body(TokenResponse.class);

      if (response == null || response.getAccessToken() == null) {
        throw new RuntimeException("Token exchange failed: empty response");
      }

      log.debug("Successfully exchanged token for audience: {}", targetAudience);
      return response.getAccessToken();
    } catch (RestClientException ex) {
      log.error("Token exchange failed for audience {}: {}", targetAudience, ex.getMessage());
      throw new RuntimeException("Token exchange failed", ex);
    } catch (Exception ex) {
      log.error(
        "Unexpected error during token exchange for audience {}: {}",
        targetAudience,
        ex.getMessage()
      );
      throw new RuntimeException("Token exchange service unavailable", ex);
    }
  }

  /**
   * Get the current user's JWT token from the security context.
   */
  @SuppressWarnings("unused")
  private String getCurrentUserToken() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
      return jwt.getTokenValue();
    }
    log.warn("No JWT found in security context");
    return null;
  }

  /**
   * Create Basic Auth header value.
   */
  private String createBasicAuthHeader(String username, String password) {
    String credentials = username + ":" + password;
    return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * DTO for OAuth2 token response
   */
  public static class TokenResponse {

    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String scope;

    // Getters and setters
    public String getAccessToken() {
      return access_token;
    }

    public void setAccess_token(String access_token) {
      this.access_token = access_token;
    }

    public String getTokenType() {
      return token_type;
    }

    public void setToken_type(String token_type) {
      this.token_type = token_type;
    }

    public Integer getExpiresIn() {
      return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
      this.expires_in = expires_in;
    }

    public String getScope() {
      return scope;
    }

    public void setScope(String scope) {
      this.scope = scope;
    }
  }
}
