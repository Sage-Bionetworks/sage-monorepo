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
          // All authentication logic is handled by GlobalFilters
          // Spring Security just provides basic web security (CORS, headers, etc.)
          .anyExchange()
          .permitAll()
      )
      .csrf(csrf -> csrf.disable())
      .build();
  }
}
