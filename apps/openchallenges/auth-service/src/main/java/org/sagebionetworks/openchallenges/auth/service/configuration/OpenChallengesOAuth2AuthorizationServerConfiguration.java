package org.sagebionetworks.openchallenges.auth.service.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * OAuth2 Authorization Server configuration for OpenChallenges.
 *
 * This configuration sets up a standards-compliant OAuth2 Authorization Server
 * with support for Authorization Code Flow, PKCE, and OpenID Connect.
 * It integrates with the existing authentication system while providing
 * standard OAuth2 endpoints for external clients.
 */
@Configuration
@ConditionalOnProperty(value = "app.oauth2.authorization-server.enabled", havingValue = "true", matchIfMissing = false)
public class OpenChallengesOAuth2AuthorizationServerConfiguration {

    /**
     * Configure the OAuth2 Authorization Server security filter chain.
     * This handles OAuth2-specific endpoints and security.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        
        // Configure the specific paths for our OAuth2 Authorization Server (unversioned)
        http.securityMatcher("/oauth2/**", "/.well-known/**");
        
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
        
        http
            // Redirect to the login page when not authenticated from the
            // authorization endpoint
            .exceptionHandling((exceptions) -> exceptions
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/v1/auth/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            )
            // Accept access tokens for User Info and/or Client Registration
            .oauth2ResourceServer((resourceServer) -> resourceServer
                .jwt(Customizer.withDefaults()));

        return http.build();
    }  /**
   * Configure registered OAuth2 clients.
   * In production, this should be externalized to a database or configuration file.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
      .clientId("openchallenges-client")
      .clientSecret("{noop}secret") // In production, use BCrypt or another encoder
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .redirectUri("http://127.0.0.1:8080/login/oauth2/code/openchallenges-oidc")
      .redirectUri("http://127.0.0.1:8080/authorized")
      .postLogoutRedirectUri("http://127.0.0.1:8080/logged-out")
      .scope(OidcScopes.OPENID)
      .scope(OidcScopes.PROFILE)
      .scope(OidcScopes.EMAIL)
      // Custom OpenChallenges scopes
      .scope("user:profile")
      .scope("user:email")
      .scope("user:keys")
      .scope("read:org")
      .scope("write:org")
      .scope("delete:org")
      .scope("admin:org")
      .scope("admin:auth")
      .scope("admin:all")
      .clientSettings(
        ClientSettings.builder()
          .requireAuthorizationConsent(true)
          .requireProofKey(true) // Enable PKCE
          .build()
      )
      .tokenSettings(
        TokenSettings.builder()
          .accessTokenTimeToLive(Duration.ofMinutes(5))
          .refreshTokenTimeToLive(Duration.ofMinutes(60))
          .reuseRefreshTokens(false) // Enhanced security - no refresh token reuse
          .build()
      )
      .build();

    return new InMemoryRegisteredClientRepository(oidcClient);
  }

  /**
   * Configure JWK source for JWT token signing.
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
      .privateKey(privateKey)
      .keyID(UUID.randomUUID().toString())
      .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  /**
   * JWT decoder for validating JWT tokens.
   */
  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  /**
   * Configure authorization server settings.
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
      .issuer("http://localhost:8087")
      .authorizationEndpoint("/oauth2/authorize")
      .tokenEndpoint("/oauth2/token")
      .tokenRevocationEndpoint("/oauth2/revoke")
      .tokenIntrospectionEndpoint("/oauth2/introspect")
      .jwkSetEndpoint("/.well-known/jwks.json")
      .oidcUserInfoEndpoint("/oauth2/userinfo")
      .oidcClientRegistrationEndpoint("/connect/register")
      .build();
  }

  /**
   * Generate an RSA key pair for JWT signing.
   */
  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }
}
