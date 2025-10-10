package org.sagebionetworks.bixarena.api.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
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
  @Valid @NotNull Auth auth
) {
  @Validated
  public record Auth(
    @NotBlank(message = "Synapse authorize URL must not be blank") String authorizeUrl,
    @NotBlank(message = "Synapse token URL must not be blank") String tokenUrl,
    @NotBlank(message = "Synapse JWKS URL must not be blank") String jwksUrl,
    @NotBlank(message = "Client id must not be blank") String clientId,
    @NotBlank(message = "Client secret must not be blank") String clientSecret,
    @NotNull(message = "Redirect URI must not be null") URI redirectUri,
    @NotBlank(message = "Internal issuer must not be blank") String internalIssuer,
    @NotBlank(message = "Audience must not be blank") String audience,
    @NotNull(message = "Token TTL seconds must not be null") Long tokenTtlSeconds
  ) {}
}
