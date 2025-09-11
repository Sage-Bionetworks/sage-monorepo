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
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User.Role;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

  private final AuthServiceProperties authServiceProperties;

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
      context.getClaims().claim("iss", authServiceProperties.getOauth2().getIssuerUrl());

      // Resolve subject and username
      SubjectInfo subjectInfo = resolveSubjectAndUsername(context, apiKeyRepository, userRepository);
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
   */
  private SubjectInfo resolveSubjectAndUsername(
    JwtEncodingContext context,
    ApiKeyRepository apiKeyRepository,
    UserRepository userRepository
  ) {
    String username = context.getPrincipal().getName();
    String subjectId = username;
    if (
      AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType()) &&
      username.startsWith("oc_api_key_")
    ) {
      try {
        var apiKeyOpt = apiKeyRepository.findByClientIdWithUser(username);
        if (apiKeyOpt.isPresent()) {
          var apiKey = apiKeyOpt.get();
          var user = apiKey.getUser();
          username = user.getUsername();
          if (user.getRole() == Role.service) {
            subjectId = apiKey.getClientId();
            log.debug("jwtTokenCustomizer: Service account - using client_id '{}' as subject for user '{}'", subjectId, username);
          } else {
            subjectId = user.getId().toString();
            log.debug("jwtTokenCustomizer: Regular user - using user ID '{}' as subject for user '{}'", subjectId, username);
          }
          log.info("Found username '{}' for API key client '{}'", username, context.getPrincipal().getName());
        } else {
          log.warn("No API key found for client ID '{}'", username);
        }
      } catch (Exception e) {
        log.error("Error looking up API key for client '{}': {}", username, e.getMessage());
      }
    } else if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
      try {
        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isPresent()) {
          var user = userOpt.get();
          subjectId = user.getId().toString();
          log.debug("jwtTokenCustomizer: Authorization code flow - using user UUID '{}' as subject for user '{}'", subjectId, username);
        } else {
          log.warn("User not found for authorization code flow: '{}'", username);
          subjectId = username;
        }
      } catch (Exception e) {
        log.error("Error looking up user for authorization code flow '{}': {}", username, e.getMessage());
        subjectId = username;
      }
    }
    return new SubjectInfo(subjectId, username);
  }

  /**
   * Adds scope claims (scp, scope) to the JWT for supported grant types.
   */
  private void addScopeClaims(JwtEncodingContext context) {
    Set<String> authorizedScopes = context.getAuthorizedScopes();
    if (authorizedScopes != null && !authorizedScopes.isEmpty()) {
      context.getClaims().claim("scp", authorizedScopes);
      context.getClaims().claim("scope", String.join(" ", authorizedScopes));
    }
  }

  /**
   * Adds the audience claim to the JWT based on the grant type and parameters.
   */
  private void addAudienceClaim(JwtEncodingContext context) {
    if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
      context.getClaims().claim("aud", authServiceProperties.getOauth2().getDefaultAudience());
      log.debug("Set default audience for authorization code flow: {}", authServiceProperties.getOauth2().getDefaultAudience());
    } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
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
    } else if ("urn:ietf:params:oauth:grant-type:token-exchange".equals(context.getAuthorizationGrantType().getValue())) {
      String resource = context.get("resource");
      if (resource != null) {
        context.getClaims().claim("aud", resource);
      }
    }
  }

  /**
   * Extracts the resource parameter from the authorization grant for client credentials flow.
   */
  private String extractResourceParameter(JwtEncodingContext context) {
    if (context.getAuthorizationGrant() instanceof OAuth2ClientCredentialsAuthenticationToken token) {
      var additionalParams = token.getAdditionalParameters();
      log.debug("jwtTokenCustomizer: Additional parameters from grant = {}", additionalParams);
      if (additionalParams != null && additionalParams.containsKey("resource")) {
        Object resourceObj = additionalParams.get("resource");
        String resourceParam = resourceObj != null ? resourceObj.toString() : null;
        log.debug("jwtTokenCustomizer: Found resource in additional parameters = {}", resourceParam);
        return resourceParam;
      }
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
    // Create JWT decoder using configured JWK endpoint
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(
      authServiceProperties.getOauth2().getJwkSetUrl()
    ).build();

    // Add audience validation for this service
    String expectedAudience = authServiceProperties.getOauth2().getDefaultAudience();
    DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
      new JwtTimestampValidator(),
      new JwtAudienceValidator(expectedAudience)
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
        return authServiceProperties.getOauth2().getIssuerUrl();
      }

      @Override
      public AuthorizationServerSettings getAuthorizationServerSettings() {
        return authorizationServerSettings();
      }
    };
  }

  /**
   * Token settings for access and refresh tokens.
   * Using configured TTL values and default signature algorithm (determined by JWK source).
   */
  @Bean
  public TokenSettings tokenSettings() {
    return TokenSettings.builder()
      .accessTokenTimeToLive(Duration.ofMinutes(authServiceProperties.getOauth2().getAccessTokenTtlMinutes()))
      .refreshTokenTimeToLive(Duration.ofHours(authServiceProperties.getOauth2().getRefreshTokenTtlHours()))
      .reuseRefreshTokens(false)
      .build();
  }

  /**
   * OAuth2 Authorization Server settings
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
      .issuer(authServiceProperties.getOauth2().getIssuerUrl())
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
