package org.sagebionetworks.openchallenges.api.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .authorizeExchange(exchanges ->
        exchanges
          // PUBLIC ENDPOINTS - no authentication required
          .pathMatchers("/actuator/health/**")
          .permitAll()
          .pathMatchers("/api/v1/auth/**")
          .permitAll()
          .pathMatchers("/api/v1/users/register")
          .permitAll()
          // PUBLIC READ ACCESS - no authentication required for GET requests
          .pathMatchers("/api/v1/challenge-analytics/**")
          .permitAll()
          .pathMatchers("/api/v1/challenge-platforms/**")
          .permitAll()
          .pathMatchers("/api/v1/edam-concepts/**")
          .permitAll()
          // Note: Authentication for other endpoints is handled by Gateway Filters
          // The filters will validate JWT/API keys and add user context headers
          // If validation fails, the filters will return 401 Unauthorized
          .anyExchange()
          .permitAll() // Let gateway filters handle authentication
      )
      .csrf(csrf -> csrf.disable())
      .build();
  }
}
