package org.sagebionetworks.openchallenges.auth.service.configuration;

import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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
    http.securityMatcher("/oauth2/**", "/.well-known/**");
    
    // Apply OAuth2 Authorization Server configuration
    http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), (authz) -> {});
    
    // Allow public access to discovery and JWK endpoints
    http.authorizeHttpRequests(authz -> authz
      .requestMatchers("/oauth2/jwks").permitAll() // Public JWK endpoint
      .requestMatchers("/.well-known/oauth-authorization-server").permitAll() // OAuth2 discovery
      .requestMatchers("/.well-known/openid_configuration").permitAll() // OIDC discovery
      .requestMatchers("/.well-known/**").permitAll() // Other well-known endpoints
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
   * Configure registered OAuth2 clients using JDBC.
   * API keys are stored as RegisteredClients with client_credentials grant.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    return new JdbcRegisteredClientRepository(jdbcTemplate);
  }

  /**
   * Configure OAuth2 token generator for access and refresh tokens.
   */
  @Bean
  public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder) {
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(jwtTokenCustomizer());
    
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  /**
   * Configure JWT token customizer to add OpenChallenges-specific claims.
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
    return context -> {
      if (context.getPrincipal() != null && context.getPrincipal().getName() != null) {
        // Add OpenChallenges-specific claims
        context.getClaims().claim("tokenType", "ACCESS");
        // Use the full issuer URL that matches the authorization server settings
        context.getClaims().claim("iss", "http://openchallenges-auth-service:8087");
        
        // Add username claim
        context.getClaims().claim("username", context.getPrincipal().getName());
        
        // You can add more custom claims here based on the user context
      }
    };
  }

  /**
   * Configure JWK source for signing JWT tokens.
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
   * Configure JWT encoder using the JWK source.
   */
  @Bean
  public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
    return new NimbusJwtEncoder(jwkSource);
  }

  /**
   * Generate RSA key pair for JWT signing.
   */
  private static KeyPair generateRsaKey() {
    KeyPairGenerator keyPairGenerator;
    try {
      keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    keyPairGenerator.initialize(2048);
    return keyPairGenerator.generateKeyPair();
  }

  /**
   * Configure OAuth2 authorization service for storing issued tokens.
   * This is essential for the introspection endpoint to validate tokens.
   */
  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  /**
   * Configure AuthorizationServerContext for OAuth2 token generation.
   */
  @Bean
  public AuthorizationServerContext authorizationServerContext() {
    return new AuthorizationServerContext() {
      @Override
      public String getIssuer() {
        return "http://openchallenges-auth-service:8087";
      }
      
      @Override
      public AuthorizationServerSettings getAuthorizationServerSettings() {
        return authorizationServerSettings();
      }
    };
  }

  /**
   * OAuth2 Authorization Server settings
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
      .issuer("http://openchallenges-auth-service:8087") // Service name for container networking
      .authorizationEndpoint("/oauth2/authorize")
      .deviceAuthorizationEndpoint("/oauth2/device_authorization")
      .deviceVerificationEndpoint("/oauth2/device_verification")
      .tokenEndpoint("/oauth2/token")
      .tokenIntrospectionEndpoint("/oauth2/introspect")
      .tokenRevocationEndpoint("/oauth2/revoke")
      .jwkSetEndpoint("/oauth2/jwks")
      .oidcLogoutEndpoint("/connect/logout")
      .oidcUserInfoEndpoint("/userinfo")
      .oidcClientRegistrationEndpoint("/connect/register")
      .build();
  }
}