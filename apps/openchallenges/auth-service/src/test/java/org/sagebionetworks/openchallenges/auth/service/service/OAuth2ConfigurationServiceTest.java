package org.sagebionetworks.openchallenges.auth.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.configuration.AuthServiceProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class OAuth2ConfigurationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  private AuthServiceProperties properties;
  private OAuth2ConfigurationService service;

  @BeforeEach
  void setUp() {
    properties = new AuthServiceProperties();
    
    // Configure Google provider
    properties.getOauth2().getGoogle().setClientId("google-client-id");
    properties.getOauth2().getGoogle().setClientSecret("google-client-secret");
    properties.getOauth2().getGoogle().setRedirectUri("https://example.com/auth/callback");
    
    // Configure Synapse provider
    properties.getOauth2().getSynapse().setClientId("synapse-client-id");
    properties.getOauth2().getSynapse().setClientSecret("synapse-client-secret");
    properties.getOauth2().getSynapse().setRedirectUri("https://example.com/auth/callback");
    
    // Configure base URL
    properties.getOauth2().setBaseUrl("https://example.com");
    
    service = new OAuth2ConfigurationService(properties);
  }

  @Nested
  @DisplayName("Provider Configuration Tests")
  class ProviderConfigurationTests {

    @Test
    @DisplayName("should return true when Google provider is configured")
    void shouldReturnTrueWhenGoogleProviderIsConfigured() {
      boolean result = service.isProviderConfigured(ExternalAccount.Provider.google);
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return true when Synapse provider is configured")
    void shouldReturnTrueWhenSynapseProviderIsConfigured() {
      boolean result = service.isProviderConfigured(ExternalAccount.Provider.synapse);
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when Google provider is not configured")
    void shouldReturnFalseWhenGoogleProviderIsNotConfigured() {
      properties.getOauth2().getGoogle().setClientId("");
      
      boolean result = service.isProviderConfigured(ExternalAccount.Provider.google);
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when provider client secret is missing")
    void shouldReturnFalseWhenProviderClientSecretIsMissing() {
      properties.getOauth2().getGoogle().setClientSecret("");
      
      boolean result = service.isProviderConfigured(ExternalAccount.Provider.google);
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("Client Configuration Tests")
  class ClientConfigurationTests {

    @Test
    @DisplayName("should return correct Google client ID")
    void shouldReturnCorrectGoogleClientId() {
      String result = service.getClientId(ExternalAccount.Provider.google);
      assertThat(result).isEqualTo("google-client-id");
    }

    @Test
    @DisplayName("should return correct Synapse client ID")
    void shouldReturnCorrectSynapseClientId() {
      String result = service.getClientId(ExternalAccount.Provider.synapse);
      assertThat(result).isEqualTo("synapse-client-id");
    }

    @Test
    @DisplayName("should return correct Google client secret")
    void shouldReturnCorrectGoogleClientSecret() {
      String result = service.getClientSecret(ExternalAccount.Provider.google);
      assertThat(result).isEqualTo("google-client-secret");
    }

    @Test
    @DisplayName("should return correct Synapse client secret")
    void shouldReturnCorrectSynapseClientSecret() {
      String result = service.getClientSecret(ExternalAccount.Provider.synapse);
      assertThat(result).isEqualTo("synapse-client-secret");
    }
  }

  @Nested
  @DisplayName("Redirect URI Tests")
  class RedirectUriTests {

    @Test
    @DisplayName("should return configured redirect URI for Google provider")
    void shouldReturnConfiguredRedirectUriForGoogleProvider() {
      String result = service.getRedirectUri(ExternalAccount.Provider.google);
      assertThat(result).isEqualTo("https://example.com/auth/callback");
    }

    @Test
    @DisplayName("should return fallback redirect URI when not configured")
    void shouldReturnFallbackRedirectUriWhenNotConfigured() {
      properties.getOauth2().getGoogle().setRedirectUri("");
      
      String result = service.getRedirectUri(ExternalAccount.Provider.google);
      assertThat(result).isEqualTo("https://example.com/auth/callback");
    }

    @Test
    @DisplayName("should handle base URL with trailing slash")
    void shouldHandleBaseUrlWithTrailingSlash() {
      properties.getOauth2().setBaseUrl("https://example.com/");
      properties.getOauth2().getGoogle().setRedirectUri("");
      
      String result = service.getRedirectUri(ExternalAccount.Provider.google);
      assertThat(result).isEqualTo("https://example.com/auth/callback");
    }
  }

  @Nested
  @DisplayName("Authorization URL Tests")
  class AuthorizationUrlTests {

    @Test
    @DisplayName("should throw exception when provider is not configured")
    void shouldThrowExceptionWhenProviderIsNotConfigured() {
      properties.getOauth2().getGoogle().setClientId("");
      
      assertThatThrownBy(() -> service.getAuthorizationUrl(ExternalAccount.Provider.google, "test-state"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("OAuth2 provider not configured");
    }
  }

  @Nested
  @DisplayName("Provider Config Tests")
  class ProviderConfigTests {

    @Test
    @DisplayName("should return null when provider is not configured")
    void shouldReturnNullWhenProviderIsNotConfigured() {
      properties.getOauth2().getGoogle().setClientId("");
      
      OAuth2ConfigurationService.OAuth2ProviderConfig result = 
        service.getProviderConfig(ExternalAccount.Provider.google);
      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("All Configured Providers Tests")
  class AllConfiguredProvidersTests {

    @Test
    @DisplayName("should return all configured providers")
    void shouldReturnAllConfiguredProviders() {
      Map<ExternalAccount.Provider, OAuth2ConfigurationService.OAuth2ProviderConfig> result = 
        service.getAllConfiguredProviders();
      
      assertThat(result).hasSize(2);
      assertThat(result).containsKeys(
        ExternalAccount.Provider.google,
        ExternalAccount.Provider.synapse
      );
    }

    @Test
    @DisplayName("should exclude unconfigured providers")
    void shouldExcludeUnconfiguredProviders() {
      properties.getOauth2().getGoogle().setClientId("");
      
      Map<ExternalAccount.Provider, OAuth2ConfigurationService.OAuth2ProviderConfig> result = 
        service.getAllConfiguredProviders();
      
      assertThat(result).hasSize(1);
      assertThat(result).containsKey(ExternalAccount.Provider.synapse);
      assertThat(result).doesNotContainKey(ExternalAccount.Provider.google);
    }
  }
}