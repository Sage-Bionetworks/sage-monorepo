package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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
public class SecurityConfiguration {

  @Value("${openchallenges.auth.jwk-set-uri:http://openchallenges-auth-service:8087/oauth2/jwks}")
  private String jwkSetUri;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Health checks always public
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
                        // Organization read operations - require read:orgs scope
            .requestMatchers(HttpMethod.GET, "/v1/organizations/**").hasAuthority("SCOPE_read:orgs")
            .requestMatchers(HttpMethod.POST, "/v1/organizations/search").hasAuthority("SCOPE_read:orgs")
            
            // Organization create operations - require create:orgs scope  
            .requestMatchers(HttpMethod.POST, "/v1/organizations").hasAuthority("SCOPE_create:orgs")
            
            // Organization update operations - require update:orgs scope
            .requestMatchers(HttpMethod.PUT, "/v1/organizations/**").hasAuthority("SCOPE_update:orgs")
            
            // Organization delete operations - require delete:orgs scope
            .requestMatchers(HttpMethod.DELETE, "/v1/organizations/**").hasAuthority("SCOPE_delete:orgs")
            .requestMatchers(HttpMethod.DELETE, "/v1/organizations/*/participations/*/role/*").hasAuthority("SCOPE_delete:org")
            
            // All other requests require authentication
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        )
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
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
