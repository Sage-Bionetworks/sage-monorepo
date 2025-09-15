package org.sagebionetworks.openchallenges.api.gateway.configuration;

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

  @Valid @NotNull Auth auth
) {
  public record Auth(
    @NotBlank(message = "Realm must not be blank") String realm,
    @NotNull(message = "Service URL must not be null") URI serviceUrl
  ) {}
}
