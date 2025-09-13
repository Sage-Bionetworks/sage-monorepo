package org.sagebionetworks.openchallenges.auth.service.service;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service for handling OAuth2 provider integrations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

  private final WebClient webClient;
  private final OAuth2ConfigurationService oAuth2ConfigurationService;

  /**
   * Exchange authorization code for access token
   */
  public OAuth2TokenResponse exchangeCodeForToken(
    ExternalAccount.Provider provider,
    String code,
    String redirectUri
  ) {
    log.debug("Exchanging authorization code for token with provider: {}", provider);

    String tokenUrl = getTokenUrl(provider);
    MultiValueMap<String, String> formData = createTokenRequestBody(provider, code, redirectUri);

    try {
      // Deserialize to Map first to avoid Jackson security restrictions
      Map<String, Object> tokenResponseMap = webClient
        .post()
        .uri(tokenUrl)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
        .timeout(Duration.ofSeconds(10))
        .block();

      // Manually map to OAuth2TokenResponse to avoid deserialization issues
      OAuth2TokenResponse response = mapToOAuth2TokenResponse(tokenResponseMap);

      log.debug("Successfully exchanged code for token with provider: {}", provider);
      log.debug(
        "Token response - access_token length: {}, token_type: {}, expires_in: {}",
        response.getAccessToken() != null ? response.getAccessToken().length() : 0,
        response.getTokenType(),
        response.getExpiresIn()
      );
      return response;
    } catch (WebClientResponseException e) {
      log.error(
        "Failed to exchange code for token with provider {}: {} - {}",
        provider,
        e.getStatusCode(),
        e.getResponseBodyAsString()
      );
      throw new RuntimeException("Failed to exchange authorization code for token", e);
    } catch (Exception e) {
      log.error(
        "Unexpected error during token exchange with provider {}: {}",
        provider,
        e.getMessage()
      );
      throw new RuntimeException("Token exchange failed", e);
    }
  }

  /**
   * Get user information from OAuth2 provider
   */
  public OAuth2UserInfo getUserInfo(ExternalAccount.Provider provider, String accessToken) {
    log.debug("Fetching user info from provider: {}", provider);

    String userInfoUrl = getUserInfoUrl(provider);
    log.info("Using user info URL for {}: {}", provider, userInfoUrl);

    try {
      // For Synapse, try a more explicit approach with detailed logging
      if (provider == ExternalAccount.Provider.synapse) {
        log.info("Making Synapse userinfo request to: {} with Bearer token", userInfoUrl);

        // Try to get response as String first for debugging
        String responseString = webClient
          .get()
          .uri(userInfoUrl)
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .header("User-Agent", "OpenChallenges-Auth-Service/1.0")
          .retrieve()
          .bodyToMono(String.class)
          .timeout(Duration.ofSeconds(10))
          .block();

        log.debug("Synapse userinfo raw string response: '{}'", responseString);

        if (responseString == null || responseString.trim().isEmpty()) {
          throw new RuntimeException("Empty response from Synapse userinfo endpoint");
        }

        // Try to parse the JSON response manually
        try {
          Map<String, Object> userInfoMap = webClient
            .get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .header("Accept", "application/json")
            .header("User-Agent", "OpenChallenges-Auth-Service/1.0")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .timeout(Duration.ofSeconds(10))
            .block();

          log.debug("Raw user info response from {}: {}", provider, userInfoMap);

          // Check for empty response
          if (userInfoMap == null || userInfoMap.isEmpty()) {
            log.error("Empty response from userinfo endpoint for {}: {}", provider, userInfoUrl);
            throw new RuntimeException("Empty response from userinfo endpoint");
          }

          // Manually map to OAuth2UserInfo to avoid deserialization issues
          OAuth2UserInfo userInfo = mapToOAuth2UserInfo(provider, userInfoMap);

          log.debug(
            "Successfully fetched user info from provider: {} for user: {}",
            provider,
            userInfo.getProviderId()
          );
          return userInfo;
        } catch (Exception parseException) {
          log.error(
            "Failed to parse Synapse userinfo response as JSON: {}",
            parseException.getMessage()
          );
          throw new RuntimeException("Failed to parse userinfo response", parseException);
        }
      } else {
        // Standard approach for other providers (Google)
        Map<String, Object> userInfoMap = webClient
          .get()
          .uri(userInfoUrl)
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
          .timeout(Duration.ofSeconds(10))
          .block();

        log.debug("Raw user info response from {}: {}", provider, userInfoMap);

        // Check for empty response
        if (userInfoMap == null || userInfoMap.isEmpty()) {
          log.error("Empty response from userinfo endpoint for {}: {}", provider, userInfoUrl);
          throw new RuntimeException("Empty response from userinfo endpoint");
        }

        // Manually map to OAuth2UserInfo to avoid deserialization issues
        OAuth2UserInfo userInfo = mapToOAuth2UserInfo(provider, userInfoMap);

        log.debug(
          "Successfully fetched user info from provider: {} for user: {}",
          provider,
          userInfo.getProviderId()
        );
        return userInfo;
      }
    } catch (WebClientResponseException e) {
      log.error(
        "Failed to fetch user info from provider {}: {} - {} - Response: {}",
        provider,
        e.getStatusCode(),
        e.getStatusText(),
        e.getResponseBodyAsString()
      );
      throw new RuntimeException("Failed to fetch user information", e);
    } catch (Exception e) {
      log.error(
        "Unexpected error during user info fetch from provider {}: {} - {}",
        provider,
        e.getMessage(),
        e.getClass().getSimpleName()
      );
      log.debug("Full stack trace for user info fetch error:", e);
      throw new RuntimeException("User info fetch failed", e);
    }
  }

  private String getTokenUrl(ExternalAccount.Provider provider) {
    // Get token endpoint from discovery document
    String tokenEndpoint = oAuth2ConfigurationService.getTokenEndpoint(provider);
    if (tokenEndpoint != null) {
      log.debug("Using token endpoint from discovery document for {}: {}", provider, tokenEndpoint);
      return tokenEndpoint;
    }

    log.error("Token endpoint not available for {} - discovery document failed", provider);
    throw new RuntimeException("Token endpoint not available for provider: " + provider);
  }

  private String getUserInfoUrl(ExternalAccount.Provider provider) {
    // Get userinfo endpoint from discovery document
    String userInfoEndpoint = oAuth2ConfigurationService.getUserInfoEndpoint(provider);
    if (userInfoEndpoint != null) {
      log.debug(
        "Using userinfo endpoint from discovery document for {}: {}",
        provider,
        userInfoEndpoint
      );
      return userInfoEndpoint;
    }

    log.error("Userinfo endpoint not available for {} - discovery document failed", provider);
    throw new RuntimeException("Userinfo endpoint not available for provider: " + provider);
  }

  private MultiValueMap<String, String> createTokenRequestBody(
    ExternalAccount.Provider provider,
    String code,
    String redirectUri
  ) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "authorization_code");
    formData.add("code", code);
    formData.add("redirect_uri", redirectUri);

    switch (provider) {
      case google:
        formData.add("client_id", oAuth2ConfigurationService.getClientId(provider));
        formData.add("client_secret", oAuth2ConfigurationService.getClientSecret(provider));
        break;
      case synapse:
        formData.add("client_id", oAuth2ConfigurationService.getClientId(provider));
        formData.add("client_secret", oAuth2ConfigurationService.getClientSecret(provider));
        break;
      default:
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
    }

    return formData;
  }

  /**
   * Maps OAuth2 token response to OAuth2TokenResponse object.
   * This avoids Jackson deserialization security issues with Map<String, Object>.
   */
  private OAuth2TokenResponse mapToOAuth2TokenResponse(Map<String, Object> tokenResponseMap) {
    return OAuth2TokenResponse.builder()
      .accessToken(getStringValue(tokenResponseMap, "access_token"))
      .tokenType(getStringValue(tokenResponseMap, "token_type"))
      .expiresIn(getLongValue(tokenResponseMap, "expires_in"))
      .refreshToken(getStringValue(tokenResponseMap, "refresh_token"))
      .scope(getStringValue(tokenResponseMap, "scope"))
      .build();
  }

  /**
   * Maps OAuth2 provider response to OAuth2UserInfo object.
   * This avoids Jackson deserialization security issues with Map<String, Object>.
   */
  private OAuth2UserInfo mapToOAuth2UserInfo(
    ExternalAccount.Provider provider,
    Map<String, Object> userInfoMap
  ) {
    log.debug(
      "Mapping user info for provider {}: {}",
      provider,
      userInfoMap != null ? userInfoMap.keySet() : "null"
    );

    if (userInfoMap == null) {
      throw new RuntimeException("User info response is null");
    }

    OAuth2UserInfo.OAuth2UserInfoBuilder builder = OAuth2UserInfo.builder();

    // Common mappings for all providers
    builder
      .sub(getStringValue(userInfoMap, "sub"))
      .id(getStringValue(userInfoMap, "id"))
      .email(getStringValue(userInfoMap, "email"))
      .emailVerified(getBooleanValue(userInfoMap, "email_verified"))
      .name(getStringValue(userInfoMap, "name"))
      .picture(getStringValue(userInfoMap, "picture"))
      .locale(getStringValue(userInfoMap, "locale"));

    // Provider-specific mappings
    switch (provider) {
      case google:
        String googleProviderId = getStringValue(userInfoMap, "sub");
        log.debug("Google provider ID from 'sub': {}", googleProviderId);
        builder
          .providerId(googleProviderId)
          .id(googleProviderId) // Set id to the same value as providerId
          .givenName(getStringValue(userInfoMap, "given_name"))
          .familyName(getStringValue(userInfoMap, "family_name"))
          .displayName(getStringValue(userInfoMap, "name"));
        break;
      case synapse:
        String synapseProviderId = getStringValue(userInfoMap, "sub");
        log.debug("Synapse provider ID from 'sub': {}", synapseProviderId);

        // If sub is null, try other common fields as fallback
        if (synapseProviderId == null) {
          synapseProviderId = getStringValue(userInfoMap, "ownerId");
          log.debug("Fallback: Synapse provider ID from 'ownerId': {}", synapseProviderId);
        }
        if (synapseProviderId == null) {
          synapseProviderId = getStringValue(userInfoMap, "userid");
          log.debug("Fallback: Synapse provider ID from 'userid': {}", synapseProviderId);
        }
        if (synapseProviderId == null) {
          synapseProviderId = getStringValue(userInfoMap, "user_id");
          log.debug("Fallback: Synapse provider ID from 'user_id': {}", synapseProviderId);
        }

        log.debug("Final Synapse provider ID: {}", synapseProviderId);
        log.debug("Available keys in Synapse userinfo: {}", userInfoMap.keySet());

        builder
          .providerId(synapseProviderId)
          .id(synapseProviderId) // Set id to the same value as providerId
          .username(getStringValue(userInfoMap, "user_name")) // Use snake_case field name
          .displayName(getStringValue(userInfoMap, "user_name")) // Use user_name as display name
          .givenName(getStringValue(userInfoMap, "given_name")) // Use snake_case field name
          .familyName(getStringValue(userInfoMap, "family_name")); // Use snake_case field name
        break;
      default:
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
    }

    OAuth2UserInfo userInfo = builder.build();
    log.debug(
      "Mapped user info - providerId: {}, email: {}",
      userInfo.getProviderId(),
      userInfo.getEmail()
    );
    return userInfo;
  }

  /**
   * Safely extract string value from map
   */
  private String getStringValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value != null ? value.toString() : null;
  }

  /**
   * Safely extract long value from map
   */
  private Long getLongValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value == null) return null;
    if (value instanceof Long) return (Long) value;
    if (value instanceof Integer) return ((Integer) value).longValue();
    if (value instanceof String) {
      try {
        return Long.parseLong((String) value);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  /**
   * Safely extract boolean value from map
   */
  private Boolean getBooleanValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value == null) return null;
    if (value instanceof Boolean) return (Boolean) value;
    if (value instanceof String) return Boolean.parseBoolean((String) value);
    return null;
  }
}
