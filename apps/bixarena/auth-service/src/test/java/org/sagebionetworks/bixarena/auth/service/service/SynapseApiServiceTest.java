package org.sagebionetworks.bixarena.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for SynapseApiService.
 * Note: Integration tests that call the real Synapse API are disabled by default.
 */
class SynapseApiServiceTest {

  private final SynapseApiService synapseApiService = new SynapseApiService();

  @Test
  @Disabled("Integration test - calls real Synapse API and requires valid access token")
  @DisplayName("Should fetch user profile from Synapse API with valid access token")
  void getUserProfile_ValidAccessToken_ReturnsProfile() {
    // Arrange - You need to provide a valid access token to run this test
    String accessToken = "YOUR_VALID_SYNAPSE_ACCESS_TOKEN";

    // Act
    SynapseApiService.SynapseUserProfile profile = synapseApiService.getUserProfile(accessToken);

    // Assert
    assertThat(profile).isNotNull();
    assertThat(profile.getOwnerId()).isNotBlank();
    assertThat(profile.getUserName()).isNotBlank();
  }

  @Test
  @DisplayName("Should handle invalid access token gracefully")
  void getUserProfile_InvalidAccessToken_ReturnsNull() {
    // Arrange
    String invalidAccessToken = "invalid_token_12345";

    // Act
    SynapseApiService.SynapseUserProfile profile = synapseApiService.getUserProfile(
      invalidAccessToken
    );

    // Assert - Should return null instead of throwing exception
    assertThat(profile).isNull();
  }

  @Test
  @DisplayName("Should handle null access token gracefully")
  void getUserProfile_NullAccessToken_ReturnsNull() {
    // Act
    SynapseApiService.SynapseUserProfile profile = synapseApiService.getUserProfile(null);

    // Assert
    assertThat(profile).isNull();
  }

  @Test
  @DisplayName("Should handle empty access token gracefully")
  void getUserProfile_EmptyAccessToken_ReturnsNull() {
    // Act
    SynapseApiService.SynapseUserProfile profile = synapseApiService.getUserProfile("");

    // Assert
    assertThat(profile).isNull();
  }
}
