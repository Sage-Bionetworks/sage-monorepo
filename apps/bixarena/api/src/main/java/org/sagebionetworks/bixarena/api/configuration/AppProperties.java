package org.sagebionetworks.bixarena.api.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Top-level application properties for BixArena API.
 * Prefix: app.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
  @NotBlank(message = "Welcome message must not be blank") String welcomeMessage,
  @Valid @NotNull AuthService authService,
  @Valid @NotNull Jwt jwt
) {
  @Validated
  public record AuthService(
    @NotBlank(message = "Auth service base URL must not be blank") String baseUrl
  ) {}

  @Validated
  public record Jwt(
    @NotBlank(message = "Expected issuer must not be blank") String expectedIssuer,
    @NotBlank(message = "Expected audience must not be blank") String expectedAudience
  ) {}
}
