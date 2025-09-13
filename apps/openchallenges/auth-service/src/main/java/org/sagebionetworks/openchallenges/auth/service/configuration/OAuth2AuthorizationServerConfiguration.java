package org.sagebionetworks.openchallenges.auth.service.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.configuration.AppProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User.Role;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken;
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
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthorizationServerConfiguration {

  private final AppProperties appProperties;

  /**
   * Validate configuration properties on startup.
   */
  @PostConstruct
  public void validateConfiguration() {
    log.info("Validating OAuth2 Authorization Server configuration...");

    try {
      // Validate OAuth2 configuration
      String issuerUrl = appProperties.getOauth2().getIssuerUrl();
      String jwkSetUrl = appProperties.getOauth2().getJwkSetUrl();
      String defaultAudience = appProperties.getOauth2().getDefaultAudience();

      if (issuerUrl == null || issuerUrl.trim().isEmpty()) {
        throw new IllegalStateException("OAuth2 issuer URL must be configured");
      }

      if (jwkSetUrl == null || jwkSetUrl.trim().isEmpty()) {
        throw new IllegalStateException("JWK set URL must be configured");
      }

      if (defaultAudience == null || defaultAudience.trim().isEmpty()) {
        throw new IllegalStateException("Default audience must be configured");
      }

      // Validate token TTL values
      if (appProperties.getOauth2().getAccessTokenTtlMinutes() <= 0) {
        throw new IllegalStateException("Access token TTL must be greater than 0");
      }

      if (appProperties.getOauth2().getRefreshTokenTtlHours() <= 0) {
        throw new IllegalStateException("Refresh token TTL must be greater than 0");
      }

      log.info("OAuth2 Authorization Server configuration is valid");
      log.info("Issuer URL: {}", issuerUrl);
      log.info(
        "Access token TTL: {} minutes",
        appProperties.getOauth2().getAccessTokenTtlMinutes()
      );
      log.info("Refresh token TTL: {} hours", appProperties.getOauth2().getRefreshTokenTtlHours());
    } catch (Exception e) {
      log.error("OAuth2 Authorization Server configuration validation failed", e);
      throw new IllegalStateException("Invalid OAuth2 configuration", e);
    }
  }

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
      .exceptionHandling(exceptions ->
        exceptions.defaultAuthenticationEntryPointFor(
          new LoginUrlAuthenticationEntryPoint("/login"),
          new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        )
      )
      .with(authorizationServerConfigurer, configurer ->
        configurer.tokenEndpoint(token ->
          token.accessTokenRequestConverter(clientCredentialsWithResourceConverter())
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
  public OAuth2TokenGenerator<?> tokenGenerator(
    JwtEncoder jwtEncoder,
    ApiKeyRepository apiKeyRepository,
    UserRepository userRepository
  ) {
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(jwtTokenCustomizer(apiKeyRepository, userRepository));

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
      var delegate = new OAuth2ClientCredentialsAuthenticationConverter();
      var auth = (OAuth2ClientCredentialsAuthenticationToken) delegate.convert(request);
      if (auth == null) return null;

      // Grab raw params from the request
      String resourceParam = request.getParameter("resource");

      log.debug("Processing client credentials request");
      log.debug("Resource parameter: {}", resourceParam);

      // Preserve the 'resource' param if present
      if (resourceParam != null && !resourceParam.trim().isEmpty()) {
        log.debug("Found resource parameter: {}", resourceParam);
        var extra = new java.util.LinkedHashMap<String, Object>(auth.getAdditionalParameters());
        extra.put("resource", resourceParam);

        return new OAuth2ClientCredentialsAuthenticationToken(
          (org.springframework.security.core.Authentication) auth.getPrincipal(),
          auth.getScopes(),
          extra
        );
      } else {
        log.debug("No resource parameter found");
      }
      return auth;
    };
  }

  /**
   * Configure JWT token customizer to add OpenChallenges-specific claims.
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(
    ApiKeyRepository apiKeyRepository,
    UserRepository userRepository
  ) {
    return context -> {
      if (context.getPrincipal() == null || context.getPrincipal().getName() == null) {
        return;
      }
      // Add OpenChallenges-specific claims
      context.getClaims().claim("tokenType", "ACCESS");
      context.getClaims().claim("iss", appProperties.getOauth2().getIssuerUrl());

      // Resolve subject and username
      SubjectInfo subjectInfo = resolveSubjectAndUsername(
        context,
        apiKeyRepository,
        userRepository
      );
      context.getClaims().claim("sub", subjectInfo.subjectId());
      context.getClaims().claim("preferred_username", subjectInfo.username());

      // Add scopes and audience claims
      addScopeClaims(context);
      addAudienceClaim(context);
    };
  }

  /**
   * Helper record for subject and username resolution.
   */
  private record SubjectInfo(String subjectId, String username) {}

  /**
   * Resolves the subject and username for the JWT based on the grant type and principal.
   * Includes comprehensive error handling for database operations.
   */
  private SubjectInfo resolveSubjectAndUsername(
    JwtEncodingContext context,
    ApiKeyRepository apiKeyRepository,
    UserRepository userRepository
  ) {
    String username = context.getPrincipal().getName();
    String subjectId = username;

    if (username == null || username.trim().isEmpty()) {
      log.warn("Principal name is null or empty, using fallback subject");
      return new SubjectInfo("unknown", "unknown");
    }

    if (
      AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType()) &&
      username.startsWith("oc_api_key_")
    ) {
      try {
        var apiKeyOpt = apiKeyRepository.findByClientIdWithUser(username);
        if (apiKeyOpt.isPresent()) {
          var apiKey = apiKeyOpt.get();
          var user = apiKey.getUser();

          if (user == null) {
            log.error("API key '{}' has no associated user", username);
            throw new OAuth2AuthenticationException(
              new OAuth2Error("invalid_client", "API key has no associated user", null)
            );
          }

          username = user.getUsername();
          if (user.getRole() == Role.service) {
            subjectId = apiKey.getClientId();
            log.debug(
              "Service account - using client_id '{}' as subject for user '{}'",
              subjectId,
              username
            );
          } else {
            subjectId = user.getId().toString();
            log.debug(
              "Regular user - using user ID '{}' as subject for user '{}'",
              subjectId,
              username
            );
          }
          log.info(
            "Found username '{}' for API key client '{}'",
            username,
            context.getPrincipal().getName()
          );
        } else {
          log.error("No API key found for client ID '{}'", username);
          throw new OAuth2AuthenticationException(
            new OAuth2Error("invalid_client", "API key not found", null)
          );
        }
      } catch (DataAccessException e) {
        log.error(
          "Database error looking up API key for client '{}': {}",
          username,
          e.getMessage()
        );
        throw new OAuth2AuthenticationException(
          new OAuth2Error("server_error", "Database error during authentication", null)
        );
      } catch (OAuth2AuthenticationException e) {
        // Re-throw OAuth2 authentication exceptions
        throw e;
      } catch (Exception e) {
        log.error(
          "Unexpected error looking up API key for client '{}': {}",
          username,
          e.getMessage(),
          e
        );
        throw new OAuth2AuthenticationException(
          new OAuth2Error("server_error", "Unexpected error during authentication", null)
        );
      }
    } else if (
      AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())
    ) {
      try {
        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isPresent()) {
          var user = userOpt.get();
          subjectId = user.getId().toString();
          log.debug(
            "Authorization code flow - using user UUID '{}' as subject for user '{}'",
            subjectId,
            username
          );
        } else {
          log.error("User not found for authorization code flow: '{}'", username);
          throw new OAuth2AuthenticationException(
            new OAuth2Error("invalid_grant", "User not found", null)
          );
        }
      } catch (DataAccessException e) {
        log.error(
          "Database error looking up user for authorization code flow '{}': {}",
          username,
          e.getMessage()
        );
        throw new OAuth2AuthenticationException(
          new OAuth2Error("server_error", "Database error during authentication", null)
        );
      } catch (OAuth2AuthenticationException e) {
        // Re-throw OAuth2 authentication exceptions
        throw e;
      } catch (Exception e) {
        log.error(
          "Unexpected error looking up user for authorization code flow '{}': {}",
          username,
          e.getMessage(),
          e
        );
        throw new OAuth2AuthenticationException(
          new OAuth2Error("server_error", "Unexpected error during authentication", null)
        );
      }
    }
    return new SubjectInfo(subjectId, username);
  }

  /**
   * Adds scope claims (scp, scope) to the JWT for supported grant types.
   * Includes validation to ensure scopes are present and valid.
   */
  private void addScopeClaims(JwtEncodingContext context) {
    Set<String> authorizedScopes = context.getAuthorizedScopes();
    if (authorizedScopes != null && !authorizedScopes.isEmpty()) {
      // Validate scopes are not empty strings
      boolean hasValidScopes = authorizedScopes
        .stream()
        .anyMatch(scope -> scope != null && !scope.trim().isEmpty());

      if (hasValidScopes) {
        context.getClaims().claim("scp", authorizedScopes);
        context.getClaims().claim("scope", String.join(" ", authorizedScopes));
        log.debug("Added scope claims: {}", authorizedScopes);
      } else {
        log.warn("All authorized scopes are null or empty, skipping scope claims");
      }
    } else {
      log.debug("No authorized scopes found, skipping scope claims");
    }
  }

  /**
   * Adds the audience claim to the JWT based on the grant type and parameters.
   */
  private void addAudienceClaim(JwtEncodingContext context) {
    if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
      context.getClaims().claim("aud", appProperties.getOauth2().getDefaultAudience());
      log.debug(
        "Set default audience for authorization code flow: {}",
        appProperties.getOauth2().getDefaultAudience()
      );
    } else if (
      AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())
    ) {
      String resourceParam = extractResourceParameter(context);
      if (resourceParam != null && !resourceParam.trim().isEmpty()) {
        context.getClaims().claim("aud", resourceParam);
        log.debug("jwtTokenCustomizer: Set audience claim to: {}", resourceParam);
      } else {
        log.error("Missing required resource parameter for client credentials flow");
        throw new OAuth2AuthenticationException(
          new OAuth2Error(
            "invalid_target",
            "The resource parameter is required for client credentials requests",
            "https://datatracker.ietf.org/doc/html/rfc8707#section-2.1"
          )
        );
      }
    } else if (
      "urn:ietf:params:oauth:grant-type:token-exchange".equals(
          context.getAuthorizationGrantType().getValue()
        )
    ) {
      String resource = context.get("resource");
      if (resource != null) {
        context.getClaims().claim("aud", resource);
      }
    }
  }

  /**
   * Extracts the resource parameter from the authorization grant for client credentials flow.
   * Includes validation and error handling for malformed grants.
   */
  private String extractResourceParameter(JwtEncodingContext context) {
    try {
      if (
        context.getAuthorizationGrant() instanceof OAuth2ClientCredentialsAuthenticationToken token
      ) {
        var additionalParams = token.getAdditionalParameters();
        log.debug("Additional parameters from grant = {}", additionalParams);

        if (additionalParams != null && additionalParams.containsKey("resource")) {
          Object resourceObj = additionalParams.get("resource");
          if (resourceObj != null) {
            String resourceParam = resourceObj.toString().trim();
            if (!resourceParam.isEmpty()) {
              log.debug("Found valid resource parameter: {}", resourceParam);
              return resourceParam;
            } else {
              log.warn("Resource parameter is empty after trimming");
            }
          } else {
            log.warn("Resource parameter is null");
          }
        } else {
          log.debug(
            "No resource parameter found in additional parameters: {}",
            additionalParams != null ? additionalParams.keySet() : "null"
          );
        }
      } else {
        log.debug(
          "Authorization grant is not a client credentials token: {}",
          context.getAuthorizationGrant() != null
            ? context.getAuthorizationGrant().getClass().getSimpleName()
            : "null"
        );
      }
    } catch (Exception e) {
      log.error(
        "Error extracting resource parameter from authorization grant: {}",
        e.getMessage(),
        e
      );
    }
    return null;
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
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    try {
      // Create JWT decoder using configured JWK endpoint
      String jwkSetUrl = appProperties.getOauth2().getJwkSetUrl();
      if (jwkSetUrl == null || jwkSetUrl.trim().isEmpty()) {
        throw new IllegalStateException("JWK set URL is not configured");
      }

      NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUrl).build();

      // Add audience validation for this service
      String expectedAudience = appProperties.getOauth2().getDefaultAudience();
      if (expectedAudience == null || expectedAudience.trim().isEmpty()) {
        throw new IllegalStateException("Default audience is not configured");
      }

      DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
        new JwtTimestampValidator(),
        new JwtAudienceValidator(expectedAudience)
      );

      jwtDecoder.setJwtValidator(validator);
      log.info(
        "JWT decoder configured with JWK set URL: {} and expected audience: {}",
        jwkSetUrl,
        expectedAudience
      );
      return jwtDecoder;
    } catch (Exception e) {
      log.error("Failed to configure JWT decoder: {}", e.getMessage(), e);
      throw new IllegalStateException("JWT decoder configuration failed", e);
    }
  }

  /**
   * Configure OAuth2 authorization service for storing issued tokens.
   * This is essential for the introspection endpoint to validate tokens.
   */
  @Bean
  public OAuth2AuthorizationService authorizationService() {
    // TODO: Store token in persistent database
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
        return appProperties.getOauth2().getIssuerUrl();
      }

      @Override
      public AuthorizationServerSettings getAuthorizationServerSettings() {
        return authorizationServerSettings();
      }
    };
  }

  /**
   * Token settings for access and refresh tokens.
   * Using configured TTL values with validation.
   */
  @Bean
  public TokenSettings tokenSettings() {
    try {
      int accessTokenTtlMinutes = appProperties.getOauth2().getAccessTokenTtlMinutes();
      int refreshTokenTtlHours = appProperties.getOauth2().getRefreshTokenTtlHours();

      if (accessTokenTtlMinutes <= 0) {
        throw new IllegalArgumentException("Access token TTL must be greater than 0 minutes");
      }

      if (refreshTokenTtlHours <= 0) {
        throw new IllegalArgumentException("Refresh token TTL must be greater than 0 hours");
      }

      TokenSettings settings = TokenSettings.builder()
        .accessTokenTimeToLive(Duration.ofMinutes(accessTokenTtlMinutes))
        .refreshTokenTimeToLive(Duration.ofHours(refreshTokenTtlHours))
        .reuseRefreshTokens(false)
        .build();

      log.info(
        "Token settings configured - Access: {} minutes, Refresh: {} hours",
        accessTokenTtlMinutes,
        refreshTokenTtlHours
      );
      return settings;
    } catch (Exception e) {
      log.error("Failed to configure token settings: {}", e.getMessage(), e);
      throw new IllegalStateException("Token settings configuration failed", e);
    }
  }

  /**
   * OAuth2 Authorization Server settings
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
      .issuer(appProperties.getOauth2().getIssuerUrl())
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
