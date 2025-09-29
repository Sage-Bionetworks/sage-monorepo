package org.sagebionetworks.openchallenges.organization.service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Organization Service as an OAuth2 Resource Server.
 * Validates JWTs directly and extracts scopes for authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AppProperties appProperties;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth ->
        auth
          // Health checks always public
          .requestMatchers("/actuator/**")
          .permitAll()
          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
          .permitAll()
          // Organization read operations - require read:organizations scope
          .requestMatchers(HttpMethod.GET, "/v1/organizations/**")
          .hasAuthority("SCOPE_read:organizations")
          .requestMatchers(HttpMethod.POST, "/v1/organizations/search")
          .hasAuthority("SCOPE_read:organizations")
          // Organization create operations - require create:organizations scope
          .requestMatchers(HttpMethod.POST, "/v1/organizations")
          .hasAuthority("SCOPE_create:organizations")
          // Organization update operations - require update:organizations scope
          .requestMatchers(HttpMethod.PUT, "/v1/organizations/**")
          .hasAuthority("SCOPE_update:organizations")
          // Organization delete operations - require delete:organizations scope
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/**")
          .hasAuthority("SCOPE_delete:organizations")
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/*/participations/*/role/*")
          .hasAuthority("SCOPE_delete:org")
          // All other requests require authentication
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(oauth2 ->
        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
      )
      .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    // Build JWK Set URI from auth service base URL
    String base = appProperties.authService().baseUrl();
    String jwkSetUri = base.endsWith("/") ? base + "oauth2/jwks" : base + "/oauth2/jwks";
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

    // Add audience validation to ensure JWTs are intended for this service
    String expectedAudience = "urn:openchallenges:organization-service";
    DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
      new JwtTimestampValidator(),
      new JwtAudienceValidator(expectedAudience)
    );

    jwtDecoder.setJwtValidator(validator);
    return jwtDecoder;
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

    // Extract scopes from the "scp" claim (preferred) or "scope" claim (fallback)
    authoritiesConverter.setAuthoritiesClaimName("scp");
    authoritiesConverter.setAuthorityPrefix("SCOPE_");

    JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

    return authenticationConverter;
  }
}
