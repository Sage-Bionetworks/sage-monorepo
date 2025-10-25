package org.sagebionetworks.bixarena.api.gateway.configuration;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.gateway.routing.RouteConfigRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security configuration for the API Gateway.
 *
 * Backend services use method-level authorization (@PreAuthorize) based on JWT scopes.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final RouteConfigRegistry routeConfigRegistry;

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    // Dynamically collect all anonymous endpoints from the route config registry
    var anonymousPaths = routeConfigRegistry.getAnonymousPaths();

    http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      // .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt) // enable if you use JWT
      .authorizeExchange(exchanges -> {
        // Always allow actuator probes
        exchanges.pathMatchers("/actuator/health", "/actuator/metrics").permitAll();

        // Guard: only register matchers when there's at least one
        if (anonymousPaths.length > 0) {
          exchanges.pathMatchers(anonymousPaths).permitAll();
        }

        // Everything else requires auth
        exchanges.anyExchange().authenticated();
      });

    return http.build();
  }
}
