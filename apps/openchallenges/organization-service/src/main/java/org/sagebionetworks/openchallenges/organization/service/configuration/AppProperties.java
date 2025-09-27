package org.sagebionetworks.openchallenges.organization.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;
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
  @NotNull @Valid ServiceProperties authService
) {
  /** Generic downstream service configuration (base URL + timeout). */
  public record ServiceProperties(
    @NotBlank(message = "Base URL must not be blank") @URL String baseUrl,
    /** Timeout in milliseconds. */
    @Positive(message = "Timeout must be positive (milliseconds)") long timeout
  ) {}
  // OAuth2 client properties removed for now (not present in application.yml). Reintroduce when needed.
}
