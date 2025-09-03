package org.sagebionetworks.openchallenges.api.gateway.configuration;

import org.sagebionetworks.openchallenges.api.gateway.security.ApiKeyAuthenticationGatewayFilter;
import org.sagebionetworks.openchallenges.api.gateway.security.JwtAuthenticationGatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security configuration for the API Gateway.
 * Implements the recommended authorization patterns with proper public/protected endpoint handling.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationGatewayFilter jwtAuthenticationFilter;
  private final ApiKeyAuthenticationGatewayFilter apiKeyAuthenticationFilter;

  public SecurityConfiguration(
      JwtAuthenticationGatewayFilter jwtAuthenticationFilter,
      ApiKeyAuthenticationGatewayFilter apiKeyAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      // Add our custom authentication filters BEFORE authorization
      .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .addFilterBefore(apiKeyAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .authorizeExchange(ex -> ex
        // Public routes – completely bypass auth
        .pathMatchers("/actuator/health", "/actuator/metrics").permitAll()
        
        // OAuth2 standard endpoints (served by auth service)
        .pathMatchers("/oauth2/**", "/.well-known/**").permitAll()
        
        // Auth service internal endpoints (used by gateway)
        .pathMatchers("/api/v1/auth/api-keys/validate").permitAll() // API key validation
        
        // Public read-only endpoints
        .pathMatchers(HttpMethod.GET, "/api/v1/challenge-analytics/**").permitAll()
        .pathMatchers(HttpMethod.GET, "/api/v1/challenge-platforms/**").permitAll()
        .pathMatchers(HttpMethod.GET, "/api/v1/edam-concepts/**").permitAll()
        
        // Read operations on domain APIs can be public (adjust as needed)
        .pathMatchers(HttpMethod.GET, "/api/v1/challenges/**").permitAll()
        // Organizations now require authentication for all operations
        .pathMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
        
        // All operations on organizations require authentication
        .pathMatchers("/api/v1/organizations/**").authenticated()
        
        // Auth service user-facing endpoints require authentication
        .pathMatchers("/api/v1/auth/profile/**").authenticated()
        .pathMatchers("/api/v1/auth/api-keys/**").authenticated()
        
        // Unsafe methods on other domain APIs require authentication
        .pathMatchers(HttpMethod.POST, "/api/v1/challenges/**", "/api/v1/images/**").authenticated()
        .pathMatchers(HttpMethod.PUT, "/api/v1/challenges/**", "/api/v1/images/**").authenticated()
        .pathMatchers(HttpMethod.DELETE, "/api/v1/challenges/**", "/api/v1/images/**").authenticated()
        .pathMatchers(HttpMethod.PATCH, "/api/v1/challenges/**", "/api/v1/images/**").authenticated()
        
        // Everything else requires authentication
        .anyExchange().authenticated()
      )
      // Authentication is handled by our custom WebFilters (JWT and API Key)
      // Spring Security handles authorization based on the presence of authentication
      .build();
  }
}
