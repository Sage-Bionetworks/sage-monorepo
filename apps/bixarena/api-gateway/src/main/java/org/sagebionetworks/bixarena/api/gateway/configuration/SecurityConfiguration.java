package org.sagebionetworks.bixarena.api.gateway.configuration;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.gateway.filter.SessionToJwtFilter;
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
 * Uses filter-based authentication (SessionToJwtFilter) to handle both anonymous
 * and authenticated routes. The filter runs BEFORE Spring Security's authorization
 * check and sets authentication context appropriately.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final SessionToJwtFilter sessionToJwtFilter;

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      // Add filter BEFORE authorization check
      .addFilterBefore(sessionToJwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
      .authorizeExchange(exchanges -> exchanges
        // Infrastructure endpoints (not in OpenAPI spec, bypassed by SessionToJwtFilter)
        .pathMatchers("/actuator/health", "/actuator/metrics", "/favicon.ico").permitAll()
        // Everything else requires authentication (set by filter)
        .anyExchange().authenticated()
      )
      .build();
  }
}
