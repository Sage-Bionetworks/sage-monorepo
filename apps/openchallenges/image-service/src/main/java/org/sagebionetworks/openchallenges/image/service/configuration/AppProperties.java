package org.sagebionetworks.openchallenges.image.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
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

  @Valid @NotNull ThumborProperties thumbor
) {
  public record ThumborProperties(
    @NotBlank(message = "Thumbor host must not be blank") String host,
    @NotNull(message = "Thumbor security key must not be blank") String securityKey,
    @Valid PlaceholderProperties placeholder
  ) {
    /**
     * Placeholder image configuration (dev convenience when using HTTP loader)
     */
    public record PlaceholderProperties(
      /** When true, the service returns a placeholder image URL instead of the real image. */
      boolean enabled,
      /**
       * Base template or URL pattern. Supported tokens: {width}, {height}. If the URL already
       * contains query parameters, tokens can still be used anywhere.
       * Example: https://images.placeholders.dev/{width}x{height}
       */
      String urlTemplate,
      /** Optional font family to pass as query param (e.g., sans-serif). */
      String fontFamily,
      /** Optional font weight (e.g., bold, 400). */
      String fontWeight,
      /** Optional fixed font size (px). If null, service leaves it unspecified so remote service picks default. */
      @Min(value = 1, message = "Font size must be positive") Integer fontSize
    ) {}
  }
}
