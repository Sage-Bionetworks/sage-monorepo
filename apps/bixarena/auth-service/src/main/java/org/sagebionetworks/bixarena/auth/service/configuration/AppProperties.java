package org.sagebionetworks.bixarena.auth.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
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
  /** Base URL of UI (Gradio) used for post-login redirect */
  @NotBlank(message = "UI base URL must not be blank") String uiBaseUrl,
  @Valid @NotNull Auth auth,
  @Valid @NotNull SessionCookie sessionCookie,
  @Valid @NotNull Cors cors
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

  @Validated
  public record SessionCookie(
    @NotBlank(message = "Cookie name must not be blank") String name,
    @NotBlank(message = "Cookie path must not be blank") String path,
    /** Cookie domain (null for host-only cookie) */
    String domain,
    @NotBlank(message = "SameSite attribute must not be blank") String sameSite,
    @NotNull(message = "Secure flag must not be null") Boolean secure,
    @NotNull(message = "HttpOnly flag must not be null") Boolean httpOnly
  ) {}

  @Validated
  public record Cors(
    @NotEmpty(message = "Allowed origins must not be empty") List<String> allowedOrigins,
    @NotEmpty(message = "Allowed methods must not be empty") List<String> allowedMethods,
    @NotEmpty(message = "Allowed headers must not be empty") List<String> allowedHeaders,
    @NotNull(message = "Allow credentials must not be null") Boolean allowCredentials
  ) {}
}
