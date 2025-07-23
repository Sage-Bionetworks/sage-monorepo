package org.sagebionetworks.openchallenges.organization.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sagebionetworks.openchallenges.organization.service.client.AuthServiceClient;
import org.sagebionetworks.openchallenges.organization.service.security.ApiKeyAuthenticationFilter;
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
          .requestMatchers(HttpMethod.GET, "/v1/organizations/**")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/v1/organizations/search")
          .permitAll()
          // Protected endpoints - require authentication and specific scopes
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/**")
          .authenticated()
          .requestMatchers(HttpMethod.POST, "/v1/organizations")
          .authenticated()
          .requestMatchers(HttpMethod.PUT, "/v1/organizations/**")
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/*/participations/*/role/*")
          .authenticated()
          .anyRequest()
          .authenticated()
      )
      .addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
      .httpBasic(httpBasic -> {});

    return http.build();
  }
}
