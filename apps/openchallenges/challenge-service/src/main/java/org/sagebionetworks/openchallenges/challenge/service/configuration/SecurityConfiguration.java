package org.sagebionetworks.openchallenges.challenge.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sagebionetworks.openchallenges.challenge.service.client.AuthServiceClient;
import org.sagebionetworks.openchallenges.challenge.service.security.ApiKeyAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  private final AuthServiceClient authServiceClient;
  private final ObjectMapper objectMapper;

  public SecurityConfiguration(AuthServiceClient authServiceClient, ObjectMapper objectMapper) {
    this.authServiceClient = authServiceClient;
    this.objectMapper = objectMapper;
  }

  @Bean
  public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
    return new ApiKeyAuthenticationFilter(authServiceClient, objectMapper);
  }

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
          .requestMatchers(HttpMethod.GET, "/v1/challenge-platforms/**")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/v1/edam-concepts/**")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/v1/challenge-analytics/**")
          .permitAll()
          // Protected endpoints - require authentication but no specific scope
          .requestMatchers(HttpMethod.DELETE, "/v1/challenge-platforms/**")
          .authenticated()
          .requestMatchers(HttpMethod.POST, "/v1/challenges/*/contributions")
          .authenticated()
          .requestMatchers(HttpMethod.PUT, "/v1/challenges/*/contributions/*")
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, "/v1/challenges/*/contributions")
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, "/v1/challenges/*/contributions/*")
          .authenticated()
          .anyRequest()
          .permitAll()
      )
      .addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
      .httpBasic(httpBasic -> {});

    return http.build();
  }
}
