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
          // ALLOWING REGISTER API FOR DIRECT ACCESS
          .pathMatchers("/api/v1/users/register")
          .permitAll()
          .pathMatchers("/api/v1/auth/login")
          .permitAll()
          .pathMatchers("/actuator/health/readiness")
          .permitAll()
          .pathMatchers("/api/v1/challenge-analytics/**")
          .permitAll()
          .pathMatchers("/api/v1/challenge-platforms/**")
          .permitAll()
          .pathMatchers("/api/v1/challenges/**")
          .permitAll()
          .pathMatchers("/api/v1/edam-concepts/**")
          .permitAll()
          .pathMatchers("/api/v1/images/**")
          .permitAll()
          .pathMatchers("/api/v1/organizations/**")
          .permitAll()
          // ALL OTHER APIS ARE AUTHENTICATED
          .anyExchange()
          .authenticated()
      )
      .csrf(csrf -> csrf.disable())
      .build();
  }
}
