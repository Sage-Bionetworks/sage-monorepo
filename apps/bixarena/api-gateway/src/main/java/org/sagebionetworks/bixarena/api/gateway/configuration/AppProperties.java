package org.sagebionetworks.bixarena.api.gateway.configuration;

import jakarta.validation.constraints.Min;
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
  @NotNull(message = "Rate limit configuration must not be null") RateLimit rateLimit
) {

  /**
   * Rate limiting configuration.
   */
  public record RateLimit(
    @NotNull(message = "Rate limit enabled flag must not be null") Boolean enabled,
    @NotNull(message = "Fail-open flag must not be null") Boolean failOpen,
    @NotNull(message = "Default requests per minute must not be null")
    @Min(value = 1, message = "Default requests per minute must be at least 1")
    Integer defaultRequestsPerMinute,
    @NotBlank(message = "Key prefix must not be blank") String keyPrefix
  ) {
    public RateLimit {
      // Provide defaults if needed
      enabled = enabled != null ? enabled : true;
      failOpen = failOpen != null ? failOpen : true;
      defaultRequestsPerMinute = defaultRequestsPerMinute != null ? defaultRequestsPerMinute : 100;
      keyPrefix = (keyPrefix != null && !keyPrefix.isBlank())
          ? keyPrefix
          : "bixarena:gateway:ratelimit";
    }
  }
}
