package org.sagebionetworks.openchallenges.auth.service.health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sagebionetworks.openchallenges.auth.service.configuration.AppProperties;

/**
 * Test for GoogleOAuth2HealthIndicator to verify OAuth2 discovery endpoint health checking.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GoogleOAuth2HealthIndicator Tests")
class GoogleOAuth2HealthIndicatorTest {

  @Mock
  private AppProperties appProperties;

  @Mock
  private AppProperties.OAuth2Config oauth2Config;

  @Mock
  private AppProperties.OAuth2Config.ProviderConfig googleConfig;

  private GoogleOAuth2HealthIndicator healthIndicator;

  private static final String DISCOVERY_URL =
    "https://accounts.google.com/.well-known/openid-configuration";

  @BeforeEach
  void setUp() {
    // Configure mocks
    when(appProperties.getOauth2()).thenReturn(oauth2Config);
    when(oauth2Config.getGoogle()).thenReturn(googleConfig);
    when(googleConfig.getDiscoveryUrl()).thenReturn(DISCOVERY_URL);

    healthIndicator = new GoogleOAuth2HealthIndicator(appProperties);
  }

  @Test
  @DisplayName("should use correct Google discovery URL")
  void shouldUseCorrectGoogleDiscoveryUrl() {
    // When
    String actualUrl = healthIndicator.getDiscoveryUrl();

    // Then
    assertEquals(DISCOVERY_URL, actualUrl);
    verify(appProperties).getOauth2();
    verify(oauth2Config).getGoogle();
    verify(googleConfig).getDiscoveryUrl();
  }

  @Test
  @DisplayName("should return correct provider name")
  void shouldReturnCorrectProviderName() {
    // When
    String providerName = healthIndicator.getProviderName();

    // Then
    assertEquals("Google OAuth2", providerName);
    // No need to verify mocks since provider name is a constant
  }
}
