package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.disable())
      .authorizeHttpRequests(auth ->
        auth
          // Public endpoints
          .requestMatchers("/actuator/**")
          .permitAll()
          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
          .permitAll()
          // All read-only endpoints are publicly accessible
          .requestMatchers(HttpMethod.GET, "/v1/challenges/**")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/v1/challengePlatforms/**")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/v1/edamConcepts/**")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/v1/challengeAnalytics/**")
          .permitAll()
          // Protected endpoints - require authentication but no specific scope
          .requestMatchers(HttpMethod.DELETE, "/v1/challengePlatforms/**")
          .authenticated()
          .anyRequest()
          .permitAll()
      )
      .httpBasic(httpBasic -> {});
    return http.build();
  }
}
