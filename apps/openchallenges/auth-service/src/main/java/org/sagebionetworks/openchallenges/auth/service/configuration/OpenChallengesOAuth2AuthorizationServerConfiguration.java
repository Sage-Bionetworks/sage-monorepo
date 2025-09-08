package org.sagebionetworks.openchallenges.auth.service.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * OAuth2 Authorization Server configuration for OpenChallenges.
 *
 * This configuration sets up a standards-compliant OAuth2 Authorization Server
 * with RSA256 JWT signing for compatibility with current Spring Security versions.
 *
 * Note: EdDSA support is limited in Spring Security OAuth2 Authorization Server.
 * See: https://github.com/spring-projects/spring-security/issues/17098
 */
@Configuration
@Slf4j
public class OpenChallengesOAuth2AuthorizationServerConfiguration {

  /**
   * Configure the OAuth2 Authorization Server security filter chain.
   * This handles OAuth2-specific endpoints and security.
   */
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();
    RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

    http
        .securityMatcher(endpointsMatcher)
        .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
        .exceptionHandling(exceptions -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
            )
        )
        .with(authorizationServerConfigurer, configurer -> 
            configurer.tokenEndpoint(token -> token
                .accessTokenRequestConverter(clientCredentialsWithResourceConverter())
            )
        );

    return http.build();
  }  /**
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
      jwtGenerator,
      accessTokenGenerator,
      refreshTokenGenerator
    );
  }

  /**
   * Custom authentication converter that preserves the 'resource' parameter
   * for RFC 8707 audience binding in client credentials flow.
   */
  @Bean
  public AuthenticationConverter clientCredentialsWithResourceConverter() {
    return request -> {
      // Let the default converter do the heavy lifting
      var delegate = new org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter();
      var auth = (org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken) delegate.convert(request);
      if (auth == null) return null;

      // Grab raw params from the request
      String resourceParam = request.getParameter("resource");
      
      log.info("🎯 AUTH CONVERTER: Processing client credentials request");
      log.info("🎯 AUTH CONVERTER: Resource parameter: {}", resourceParam);
      
      // Preserve the 'resource' param if present
      if (resourceParam != null && !resourceParam.trim().isEmpty()) {
        log.info("🎯 AUTH CONVERTER: Found resource parameter: {}", resourceParam);
        var extra = new java.util.LinkedHashMap<String, Object>(auth.getAdditionalParameters());
        extra.put("resource", resourceParam);
        
        return new org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken(
            (org.springframework.security.core.Authentication) auth.getPrincipal(), 
            auth.getScopes(), 
            extra);
      } else {
        log.info("🎯 AUTH CONVERTER: No resource parameter found");
      }
      return auth;
    };
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

        // Add scopes for client credentials flow
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
          Set<String> authorizedScopes = context.getAuthorizedScopes();
          if (authorizedScopes != null && !authorizedScopes.isEmpty()) {
            // Add scopes as both 'scp' (standard) and 'scope' (for Spring Security compatibility)
            context.getClaims().claim("scp", authorizedScopes);
            context.getClaims().claim("scope", String.join(" ", authorizedScopes));
          }
          
          // RFC 8707 style audience/resource binding - read from authorization grant
          String resourceParam = null;
          
          // Get resource parameter from the authorization grant's additional parameters
          if (context.getAuthorizationGrant() instanceof org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken) {
            var clientCredentialsToken = (org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken) context.getAuthorizationGrant();
            var additionalParams = clientCredentialsToken.getAdditionalParameters();
            
            log.info("🎯 JWT CUSTOMIZER: Additional parameters from grant = {}", additionalParams);
            
            if (additionalParams != null && additionalParams.containsKey("resource")) {
              Object resourceObj = additionalParams.get("resource");
              resourceParam = resourceObj != null ? resourceObj.toString() : null;
              log.info("🎯 JWT CUSTOMIZER: Found resource in additional parameters = {}", resourceParam);
            }
          }
          
          log.info("🎯 JWT CUSTOMIZER: Final resource parameter = {}", resourceParam);
          
          // RFC 8707: resource parameter is required for all API key requests
          if (resourceParam != null && !resourceParam.trim().isEmpty()) {
            context.getClaims().claim("aud", resourceParam);
            log.info("🎯 JWT CUSTOMIZER: Set audience claim to: {}", resourceParam);
          } else {
            // RFC 8707: Missing resource parameter should result in invalid_target error
            log.error("🎯 JWT CUSTOMIZER: Missing required resource parameter for client credentials flow");
            throw new org.springframework.security.oauth2.core.OAuth2AuthenticationException(
                new org.springframework.security.oauth2.core.OAuth2Error(
                    "invalid_target", 
                    "The resource parameter is required for client credentials requests", 
                    "https://datatracker.ietf.org/doc/html/rfc8707#section-2.1"
                )
            );
          }
        }
        
        // Add scopes for token exchange flow (RFC 8693)
        if ("urn:ietf:params:oauth:grant-type:token-exchange".equals(context.getAuthorizationGrantType().getValue())) {
          Set<String> authorizedScopes = context.getAuthorizedScopes();
          if (authorizedScopes != null && !authorizedScopes.isEmpty()) {
            // Add scopes as both 'scp' (standard) and 'scope' (for Spring Security compatibility)
            context.getClaims().claim("scp", authorizedScopes);
            context.getClaims().claim("scope", String.join(" ", authorizedScopes));
          }
          
          // For token exchange, we may want to add audience claim
          String resource = context.get("resource");
          if (resource != null) {
            context.getClaims().claim("aud", resource);
          }
        }
        // You can add more custom claims here based on the user context
      }
    };
  }

  /**
   * Configure JWK source for RSA256 JWT signing.
   *
   * Note: EdDSA support is limited in Spring Security OAuth2 Authorization Server.
   * See: https://github.com/spring-projects/spring-security/issues/17098
   *
   * Using RSA256 for compatibility until Spring Security 7.0 / Spring Authorization Server 2.0
   * adds proper EdDSA support.
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    try {
      // Generate RSA key pair for JWT signing
      RSAKey rsaKey = new RSAKeyGenerator(2048)
        .keyID(UUID.randomUUID().toString())
        .algorithm(JWSAlgorithm.RS256)
        .keyUse(KeyUse.SIGNATURE)
        .generate();

      // Create JWK set and return source
      JWKSet jwkSet = new JWKSet(rsaKey);
      return new ImmutableJWKSet<>(jwkSet);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to generate RSA key for JWT signing", ex);
    }
  }

  /**
   * Configure JWT encoder using the JWK source.
   */
  @Bean
  public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
    return new NimbusJwtEncoder(jwkSource);
  }

  /**
   * Configure JWT decoder for web authentication using the same JWK source.
   * This allows web pages to validate OAuth2 JWTs stored in cookies.
   * Includes audience validation to ensure tokens are intended for this service.
   */
  @Bean
  public org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder(
    JWKSource<SecurityContext> jwkSource
  ) {
    // Create JWT decoder using HTTP JWK endpoint 
    org.springframework.security.oauth2.jwt.NimbusJwtDecoder jwtDecoder = 
      org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri(
        "http://localhost:8087/oauth2/jwks"
      ).build();

    // Add audience validation for this service
    String expectedAudience = "urn:openchallenges:auth-service";
    org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> validator =
      new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
        new org.springframework.security.oauth2.jwt.JwtTimestampValidator(),
        new org.springframework.security.oauth2.jwt.JwtAudienceValidator(expectedAudience)
      );
    
    jwtDecoder.setJwtValidator(validator);
    return jwtDecoder;
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
   * Token settings for access and refresh tokens.
   * Using default signature algorithm (will be determined by JWK source).
   */
  @Bean
  public TokenSettings tokenSettings() {
    return TokenSettings.builder()
      .accessTokenTimeToLive(Duration.ofMinutes(60)) // 1 hour
      .refreshTokenTimeToLive(Duration.ofHours(24)) // 24 hours
      .reuseRefreshTokens(false)
      .build();
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
