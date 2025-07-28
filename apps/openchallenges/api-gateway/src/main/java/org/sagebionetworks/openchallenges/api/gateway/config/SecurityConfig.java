package org.sagebionetworks.openchallenges.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http
      .authorizeExchange()
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
      .and()
      .csrf()
      .disable();
    return http.build();
  }
}
