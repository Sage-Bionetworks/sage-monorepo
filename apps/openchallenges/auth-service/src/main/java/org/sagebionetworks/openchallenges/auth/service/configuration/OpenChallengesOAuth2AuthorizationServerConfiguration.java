package org.sagebionetworks.openchallenges.auth.service.configuration;

import java.time.Duration;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * OAuth2 Authorization Server configuration for OpenChallenges.
 * 
 * This configuration sets up a standards-compliant OAuth2 Authorization Server
 * without OIDC discovery support for simplicity and reliability.
 */
@Configuration
public class OpenChallengesOAuth2AuthorizationServerConfiguration {

  /**
   * Configure the OAuth2 Authorization Server security filter chain.
   * This handles OAuth2-specific endpoints and security.
   */
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
    throws Exception {
    
    // Configure the specific paths for our OAuth2 Authorization Server
    http.securityMatcher("/oauth2/**");
    
    // Apply OAuth2 Authorization Server configuration
    http.apply(new OAuth2AuthorizationServerConfigurer());
    
    // Allow public access to JWK endpoint
    http.authorizeHttpRequests(authz -> authz
      .requestMatchers("/oauth2/jwks").permitAll() // Public JWK endpoint
      .anyRequest().authenticated() // All other OAuth2 endpoints require auth
    );
    
    // Configure exception handling for login redirection
    http.exceptionHandling(exceptions ->
      exceptions.defaultAuthenticationEntryPointFor(
        new LoginUrlAuthenticationEntryPoint("/login"),
        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
      )
    );
    
    return http.build();
  }

  /**
   * Configure registered OAuth2 clients.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
      .clientId("openchallenges-client")
      .clientSecret("{noop}secret") // In production, use BCrypt
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
      .scope("user:profile")
      .scope("user:email")
      .scope("user:keys")
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
          .reuseRefreshTokens(false)
          .build()
      )
      .build();

    return new InMemoryRegisteredClientRepository(oidcClient);
  }
}