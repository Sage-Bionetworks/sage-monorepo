package org.sagebionetworks.openchallenges.auth.service.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pure unit tests for ApiKeyProperties.
 * Tests the basic functionality without Spring context or external dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ApiKeyPropertiesTest {

  @Test
  @DisplayName("should have default values when created")
  void shouldHaveDefaultValuesWhenCreated() {
    // Arrange & Act
    ApiKeyProperties properties = new ApiKeyProperties();

    // Assert
    assertThat(properties.getPrefix()).isEqualTo("oc_dev_");
    assertThat(properties.getLength()).isEqualTo(40);
  }

  @Test
  @DisplayName("should allow setting custom prefix")
  void shouldAllowSettingCustomPrefix() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setPrefix("oc_prod_");

    // Assert
    assertThat(properties.getPrefix()).isEqualTo("oc_prod_");
  }

  @Test
  @DisplayName("should allow setting custom length")
  void shouldAllowSettingCustomLength() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setLength(60);

    // Assert
    assertThat(properties.getLength()).isEqualTo(60);
  }

  @Test
  @DisplayName("should allow setting both prefix and length")
  void shouldAllowSettingBothPrefixAndLength() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setPrefix("custom_");
    properties.setLength(32);

    // Assert
    assertThat(properties.getPrefix()).isEqualTo("custom_");
    assertThat(properties.getLength()).isEqualTo(32);
  }

  @Test
  @DisplayName("should handle null prefix gracefully")
  void shouldHandleNullPrefixGracefully() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setPrefix(null);

    // Assert
    assertThat(properties.getPrefix()).isNull();
  }

  @Test
  @DisplayName("should handle zero length")
  void shouldHandleZeroLength() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setLength(0);

    // Assert
    assertThat(properties.getLength()).isEqualTo(0);
  }

  @Test
  @DisplayName("should handle negative length")
  void shouldHandleNegativeLength() {
    // Arrange
    ApiKeyProperties properties = new ApiKeyProperties();

    // Act
    properties.setLength(-5);

    // Assert
    assertThat(properties.getLength()).isEqualTo(-5);
  }
}
