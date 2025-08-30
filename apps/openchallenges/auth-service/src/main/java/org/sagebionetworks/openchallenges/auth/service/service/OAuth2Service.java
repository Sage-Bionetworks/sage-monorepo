package org.sagebionetworks.openchallenges.auth.service.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
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
      OAuth2TokenResponse response = webClient
        .post()
        .uri(tokenUrl)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(OAuth2TokenResponse.class)
        .timeout(Duration.ofSeconds(10))
        .block();

      log.debug("Successfully exchanged code for token with provider: {}", provider);
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

    try {
      OAuth2UserInfo userInfo = webClient
        .get()
        .uri(userInfoUrl)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .bodyToMono(OAuth2UserInfo.class)
        .timeout(Duration.ofSeconds(10))
        .block();

      log.debug(
        "Successfully fetched user info from provider: {} for user: {}",
        provider,
        userInfo.getProviderId()
      );
      return userInfo;
    } catch (WebClientResponseException e) {
      log.error(
        "Failed to fetch user info from provider {}: {} - {}",
        provider,
        e.getStatusCode(),
        e.getResponseBodyAsString()
      );
      throw new RuntimeException("Failed to fetch user information", e);
    } catch (Exception e) {
      log.error(
        "Unexpected error during user info fetch from provider {}: {}",
        provider,
        e.getMessage()
      );
      throw new RuntimeException("User info fetch failed", e);
    }
  }

  private String getTokenUrl(ExternalAccount.Provider provider) {
    switch (provider) {
      case google:
        return "https://oauth2.googleapis.com/token";
      case synapse:
        return "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token";
      default:
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
    }
  }

  private String getUserInfoUrl(ExternalAccount.Provider provider) {
    switch (provider) {
      case google:
        return "https://www.googleapis.com/oauth2/v2/userinfo";
      case synapse:
        return "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo";
      default:
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
    }
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
}
