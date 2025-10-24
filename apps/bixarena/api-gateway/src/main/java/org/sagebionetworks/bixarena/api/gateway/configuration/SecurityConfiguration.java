package org.sagebionetworks.bixarena.api.gateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

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

  private final org.sagebionetworks.bixarena.api.gateway.security.AnonymousAccessGatewayFilter anonymousAccessFilter;

  @Bean
  public org.sagebionetworks.bixarena.api.gateway.security.AnonymousAccessGatewayFilter anonymousAccessGatewayFilter() {
    java.util.Set<String> anonymousEndpoints = java.util.Set.of(
      "/.well-known/jwks.json",
      "/oauth2/token",
      "/auth/login",
      "/auth/callback",
      "/auth/logout",
      "/userinfo"
    );
    return new org.sagebionetworks.bixarena.api.gateway.security.AnonymousAccessGatewayFilter(
      anonymousEndpoints
    );
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .addFilterBefore(anonymousAccessGatewayFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
      .authorizeExchange(ex ->
        ex
          .pathMatchers("/actuator/health", "/actuator/metrics")
          .permitAll()
          .pathMatchers(
            "/.well-known/jwks.json",
            "/oauth2/token",
            "/auth/login",
            "/auth/callback",
            "/auth/logout",
            "/userinfo"
          )
          .permitAll()
          .pathMatchers("/api/v1/leaderboards/**")
          .permitAll()
          .anyExchange()
          .authenticated()
      )
      .build();
  }
}
