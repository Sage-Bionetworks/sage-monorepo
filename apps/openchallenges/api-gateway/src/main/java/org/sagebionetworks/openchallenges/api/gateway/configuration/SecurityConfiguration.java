package org.sagebionetworks.openchallenges.api.gateway.configuration;

import org.sagebionetworks.openchallenges.api.gateway.security.AnonymousAccessGatewayFilter;
import org.sagebionetworks.openchallenges.api.gateway.security.ApiKeyAuthenticationGatewayFilter;
import org.sagebionetworks.openchallenges.api.gateway.security.JwtAuthenticationGatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security configuration for the API Gateway.
 * 
 * All API endpoints now require JWT authentication, which is provided by:
 * 1. AnonymousAccessGatewayFilter - generates JWTs for public endpoints
 * 2. ApiKeyAuthenticationGatewayFilter - exchanges API keys for JWTs
 * 3. JwtAuthenticationGatewayFilter - validates existing JWTs
 * 
 * Backend services use method-level authorization (@PreAuthorize) based on JWT scopes.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AnonymousAccessGatewayFilter anonymousAccessFilter;
  private final ApiKeyAuthenticationGatewayFilter apiKeyAuthenticationFilter;
  private final JwtAuthenticationGatewayFilter jwtAuthenticationFilter;

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      // Add our custom authentication filters in order:
      // 1. Anonymous access (generates JWTs for public endpoints)
      // 2. API key authentication (exchanges API keys for JWTs)  
      // 3. JWT validation (validates existing JWTs)
      .addFilterBefore(anonymousAccessFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .addFilterBefore(apiKeyAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .authorizeExchange(ex -> ex
        // Actuator endpoints for health checks
        .pathMatchers("/actuator/health", "/actuator/metrics").permitAll()
        
        // OAuth2 endpoints (served by auth service)
        .pathMatchers("/oauth2/**", "/.well-known/**").permitAll()
        
        // All API endpoints require JWT authentication
        // Authentication is provided by our custom filters:
        // - Anonymous access filter generates JWTs for public endpoints  
        // - API key filter exchanges API keys for JWTs
        // - JWT filter validates existing JWTs
        // Backend services handle authorization via @PreAuthorize with JWT scopes
        .pathMatchers("/api/**").authenticated()
        
        // Everything else requires authentication
        .anyExchange().authenticated()
      )
      .build();
  }
}
