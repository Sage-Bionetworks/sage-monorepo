package org.sagebionetworks.openchallenges.auth.service.service;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OAuth2ConfigurationService {

  private final String googleClientId;
  private final String googleClientSecret;
  private final String synapseClientId;
  private final String synapseClientSecret;
  private final String baseUrl;
  private final RestTemplate restTemplate;

  // Discovery endpoint URLs
  private static final String GOOGLE_DISCOVERY_URL =
    "https://accounts.google.com/.well-known/openid-configuration";
  private static final String SYNAPSE_DISCOVERY_URL =
    "https://repo-prod.prod.sagebase.org/auth/v1/.well-known/openid-configuration";

  // Cache for discovery documents to avoid repeated requests
  private volatile Map<String, Object> googleDiscovery;
  private volatile Map<String, Object> synapseDiscovery;

  public OAuth2ConfigurationService(
    @Value("${openchallenges.auth-service.oauth2.google.client-id:}") String googleClientId,
    @Value("${openchallenges.auth-service.oauth2.google.client-secret:}") String googleClientSecret,
    @Value("${openchallenges.auth-service.oauth2.synapse.client-id:}") String synapseClientId,
    @Value(
      "${openchallenges.auth-service.oauth2.synapse.client-secret:}"
    ) String synapseClientSecret,
    @Value("${openchallenges.auth-service.base-url:http://localhost:8087}") String baseUrl
  ) {
    this.googleClientId = googleClientId;
    this.googleClientSecret = googleClientSecret;
    this.synapseClientId = synapseClientId;
    this.synapseClientSecret = synapseClientSecret;
    this.baseUrl = baseUrl;
    this.restTemplate = new RestTemplate();

    log.info("OAuth2 Configuration Service initialized for base URL: {}", baseUrl);
    log.info(
      "Google OAuth2 enabled: {} (client ID: {})",
      !googleClientId.isEmpty(),
      googleClientId.isEmpty()
        ? "NOT_SET"
        : googleClientId.substring(0, Math.min(10, googleClientId.length())) + "..."
    );
    log.info("Synapse OAuth2 enabled: {}", !synapseClientId.isEmpty());
  }

  /**
   * Get OAuth2 authorization URL for provider
   */
  public String getAuthorizationUrl(ExternalAccount.Provider provider, String state) {
    log.debug("Generating authorization URL for provider: {}", provider);

    if (!isProviderConfigured(provider)) {
      log.error(
        "OAuth2 provider not configured: {}. Client ID empty: {}",
        provider,
        provider == ExternalAccount.Provider.google
          ? googleClientId.isEmpty()
          : synapseClientId.isEmpty()
      );
      throw new IllegalArgumentException("OAuth2 provider not configured: " + provider);
    }

    String redirectUri = getRedirectUri(provider);
    log.debug("Using redirect URI: {}", redirectUri);

    // Get authorization endpoint from discovery document
    String authorizationEndpoint = getAuthorizationEndpoint(provider);
    if (authorizationEndpoint == null) {
      log.error("Unable to retrieve authorization endpoint for provider: {}", provider);
      throw new RuntimeException("Authorization endpoint not available for provider: " + provider);
    }

    String clientId =
      switch (provider) {
        case google -> googleClientId;
        case synapse -> synapseClientId;
      };

    // Build authorization URL with proper parameters
    String authUrl =
      switch (provider) {
        case google -> String.format(
          "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&access_type=offline&prompt=select_account&state=%s",
          authorizationEndpoint,
          clientId,
          redirectUri,
          "openid%20email%20profile",
          state
        );
        case synapse -> String.format(
          "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&state=%s",
          authorizationEndpoint,
          clientId,
          redirectUri,
          "openid%20email%20profile",
          state
        );
      };

    log.debug("Generated authorization URL: {}", authUrl);
    return authUrl;
  }

  /**
   * Get authorization endpoint from discovery document
   */
  private String getAuthorizationEndpoint(ExternalAccount.Provider provider) {
    Map<String, Object> discovery = getDiscoveryDocument(provider);
    return discovery != null ? (String) discovery.get("authorization_endpoint") : null;
  }

  /**
   * Get discovery document for OAuth2 provider
   */
  private Map<String, Object> getDiscoveryDocument(ExternalAccount.Provider provider) {
    try {
      return switch (provider) {
        case google -> {
          if (googleDiscovery == null) {
            log.debug("Fetching Google OAuth2 discovery document");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
              GOOGLE_DISCOVERY_URL,
              HttpMethod.GET,
              null,
              new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            googleDiscovery = response.getBody();
            log.debug("Google discovery document cached successfully");
          }
          yield googleDiscovery;
        }
        case synapse -> {
          if (synapseDiscovery == null) {
            log.debug("Fetching Synapse OAuth2 discovery document");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
              SYNAPSE_DISCOVERY_URL,
              HttpMethod.GET,
              null,
              new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            synapseDiscovery = response.getBody();
            log.debug("Synapse discovery document cached successfully");
          }
          yield synapseDiscovery;
        }
      };
    } catch (RestClientException e) {
      log.error("Failed to fetch discovery document for provider {}: {}", provider, e.getMessage());
      return null;
    }
  }

  /**
   * Get redirect URI for provider
   */
  public String getRedirectUri(ExternalAccount.Provider provider) {
    return String.format("%s/auth/callback", baseUrl.replaceAll("/$", ""));
  }

  /**
   * Check if provider is configured
   */
  public boolean isProviderConfigured(ExternalAccount.Provider provider) {
    return switch (provider) {
      case google -> !googleClientId.isEmpty() && !googleClientSecret.isEmpty();
      case synapse -> !synapseClientId.isEmpty() && !synapseClientSecret.isEmpty();
    };
  }

  /**
   * Get provider configuration details
   */
  public OAuth2ProviderConfig getProviderConfig(ExternalAccount.Provider provider) {
    if (!isProviderConfigured(provider)) {
      return null;
    }

    return switch (provider) {
      case google -> OAuth2ProviderConfig.builder()
        .provider(provider)
        .clientId(googleClientId)
        .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
        .tokenUri("https://oauth2.googleapis.com/token")
        .userInfoUri("https://www.googleapis.com/oauth2/v1/userinfo")
        .redirectUri(getRedirectUri(provider))
        .scopes(java.util.Set.of("openid", "email", "profile"))
        .build();
      case synapse -> OAuth2ProviderConfig.builder()
        .provider(provider)
        .clientId(synapseClientId)
        .authorizationUri("https://signin.synapse.org/oauth2/authorize")
        .tokenUri("https://signin.synapse.org/oauth2/token")
        .userInfoUri("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo")
        .redirectUri(getRedirectUri(provider))
        .scopes(java.util.Set.of("openid", "email", "profile"))
        .build();
    };
  }

  /**
   * Get all configured providers
   */
  public Map<ExternalAccount.Provider, OAuth2ProviderConfig> getAllConfiguredProviders() {
    Map<ExternalAccount.Provider, OAuth2ProviderConfig> configMap = new HashMap<>();

    OAuth2ProviderConfig googleConfig = getProviderConfig(ExternalAccount.Provider.google);
    if (googleConfig != null) {
      configMap.put(ExternalAccount.Provider.google, googleConfig);
    }

    OAuth2ProviderConfig synapseConfig = getProviderConfig(ExternalAccount.Provider.synapse);
    if (synapseConfig != null) {
      configMap.put(ExternalAccount.Provider.synapse, synapseConfig);
    }

    return configMap;
  }

  /**
   * Get user info endpoint for provider
   */
  public String getUserInfoEndpoint(ExternalAccount.Provider provider) {
    OAuth2ProviderConfig config = getProviderConfig(provider);
    return config != null ? config.getUserInfoUri() : null;
  }

  /**
   * Get token endpoint for provider
   */
  public String getTokenEndpoint(ExternalAccount.Provider provider) {
    OAuth2ProviderConfig config = getProviderConfig(provider);
    return config != null ? config.getTokenUri() : null;
  }

  /**
   * Get client ID for provider
   */
  public String getClientId(ExternalAccount.Provider provider) {
    return switch (provider) {
      case google -> googleClientId;
      case synapse -> synapseClientId;
    };
  }

  /**
   * Get client secret for provider
   */
  public String getClientSecret(ExternalAccount.Provider provider) {
    return switch (provider) {
      case google -> googleClientSecret;
      case synapse -> synapseClientSecret;
    };
  }

  /**
   * OAuth2 Provider Configuration
   */
  @lombok.Builder
  @lombok.Data
  public static class OAuth2ProviderConfig {

    private ExternalAccount.Provider provider;
    private String clientId;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String redirectUri;
    private java.util.Set<String> scopes;
  }
}
