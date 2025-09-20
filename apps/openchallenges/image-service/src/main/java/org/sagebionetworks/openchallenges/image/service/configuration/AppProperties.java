package org.sagebionetworks.openchallenges.image.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties.
 *
 * All properties are prefixed with 'app.'
 * Properties are validated on application startup.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
  @NotBlank(message = "Welcome message must not be blank") String welcomeMessage,

  @Valid @NotNull ThumborProperties thumbor,

  @Valid PlaceholderProperties placeholder
) {
  public record ThumborProperties(
    @NotBlank(message = "Thumbor host must not be blank") String host,
    @NotNull(message = "Thumbor security key must not be blank") String securityKey
  ) {}

  /**
   * Placeholder image configuration (independent of Thumbor). If enabled, the service ignores
   * the object key / imageUrl and returns a direct remote placeholder URL.
   */
  public record PlaceholderProperties(
    boolean enabled,
    /**
     * Base template or URL pattern. Supported tokens: {width}, {height}. If the template already
     * includes query params (e.g. fontFamily, fontWeight, fontSize), they are passed through.
     * Example: https://images.placeholders.dev/{width}x{height}?fontFamily=sans-serif&fontWeight=bold
     */
    String urlTemplate
  ) {}
}
