package org.sagebionetworks.openchallenges.image.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
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
    /**
     * When true (intended for local development when using the Thumbor HTTP loader), the image
     * service will ignore the requested object key / image URL and instead return a placeholder
     * image URL sized according to the requested height and aspect ratio. This avoids having to
     * first upload images to S3.
     */
    boolean usePlaceholderImages,
    /**
     * Template of the placeholder image service. Supported tokens: {width}, {height}.
     * Example: https://images.placeholders.dev/{width}x{height}
     * If null or blank while usePlaceholderImages is true, a built-in default template will be used.
     */
    String placeholderUrlTemplate
  ) {}
}
