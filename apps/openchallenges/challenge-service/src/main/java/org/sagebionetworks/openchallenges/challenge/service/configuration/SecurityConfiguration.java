package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collections;
import java.util.List;

/**
 * Security configuration for the Challenge Service as an OAuth2 Resource Server.
 * Validates JWTs directly and extracts scopes for authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

  @Value("${openchallenges.auth.jwk-set-uri:http://openchallenges-auth-service:8087/oauth2/jwks}")
  private String jwkSetUri;

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
          // Challenge read operations - require read:challenges scope
          .requestMatchers(HttpMethod.GET, "/v1/challenges/**")
          .hasAuthority("SCOPE_read:challenges")
          // Challenge create operations - require create:challenges scope
          .requestMatchers(HttpMethod.POST, "/v1/challenges")
          .hasAuthority("SCOPE_create:challenges")
          // Challenge update operations - require update:challenges scope
          .requestMatchers(HttpMethod.PUT, "/v1/challenges/**")
          .hasAuthority("SCOPE_update:challenges")
          .requestMatchers(HttpMethod.POST, "/v1/challenges/*/contributions")
          .hasAuthority("SCOPE_update:challenges")
          .requestMatchers(HttpMethod.PUT, "/v1/challenges/*/contributions/*")
          .hasAuthority("SCOPE_update:challenges")
          .requestMatchers(HttpMethod.DELETE, "/v1/challenges/*/contributions")
          .hasAuthority("SCOPE_update:challenges")
          .requestMatchers(HttpMethod.DELETE, "/v1/challenges/*/contributions/*")
          .hasAuthority("SCOPE_update:challenges")
          // Challenge delete operations - require delete:challenges scope
          .requestMatchers(HttpMethod.DELETE, "/v1/challenges/**")
          .hasAuthority("SCOPE_delete:challenges")
          // Challenge analytics read operations - require read:challenges-analytics scope
          .requestMatchers(HttpMethod.GET, "/v1/challenge-analytics/**")
          .hasAuthority("SCOPE_read:challenges-analytics")
          // Challenge platforms read operations - require read:challenge-platforms scope
          .requestMatchers(HttpMethod.GET, "/v1/challenge-platforms/**")
          .hasAuthority("SCOPE_read:challenge-platforms")
          // Challenge platforms create operations - require create:challenge-platforms scope
          .requestMatchers(HttpMethod.POST, "/v1/challenge-platforms")
          .hasAuthority("SCOPE_create:challenge-platforms")
          // Challenge platforms update operations - require update:challenge-platforms scope
          .requestMatchers(HttpMethod.PUT, "/v1/challenge-platforms/**")
          .hasAuthority("SCOPE_update:challenge-platforms")
          // Challenge platforms delete operations - require delete:challenge-platforms scope
          .requestMatchers(HttpMethod.DELETE, "/v1/challenge-platforms/**")
          .hasAuthority("SCOPE_delete:challenge-platforms")
          // EDAM concepts read operations - require read:edam-concepts scope
          .requestMatchers(HttpMethod.GET, "/v1/edam-concepts/**")
          .hasAuthority("SCOPE_read:edam-concepts")
          // All other requests require authentication
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(oauth2 ->
        oauth2.jwt(jwt ->
          jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter())
        )
      )
      .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    // Create JWT decoder with audience validation for challenge service
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    
    // Add audience validation to ensure JWTs are intended for this service
    String expectedAudience = "urn:openchallenges:challenge-service";
    OAuth2TokenValidator<Jwt> compositeValidator = new DelegatingOAuth2TokenValidator<>(
        new JwtTimestampValidator(),
        new JwtAudienceValidator(expectedAudience)
    );
    
    jwtDecoder.setJwtValidator(compositeValidator);
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
