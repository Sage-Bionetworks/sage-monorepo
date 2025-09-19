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
    @NotNull(message = "Thumbor security key must not be blank") URI securityKey
  ) {}
}
