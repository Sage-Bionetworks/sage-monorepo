package org.sagebionetworks.openchallenges.auth.service.configuration;

import org.sagebionetworks.openchallenges.auth.service.security.ApiKeyAuthenticationFilter;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  /**
   * Configure BCrypt password encoder for hashing passwords and API keys
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strength 12 for good security
  }

  /**
   * Configure security filter chain with API key authentication
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyService apiKeyService)
    throws Exception {
    // Create the API key authentication filter here to avoid circular dependency
    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
      apiKeyService
    );

    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for API
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless for API
      .authorizeHttpRequests(
        authz ->
          authz
            .requestMatchers("/v1/auth/login", "/v1/auth/validate")
            .permitAll() // Public endpoints
            .requestMatchers("/actuator/health", "/actuator/info")
            .permitAll() // Health checks
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .permitAll() // OpenAPI docs
            .anyRequest()
            .authenticated() // All other endpoints require authentication
      )
      .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add API key filter

    return http.build();
  }
}
