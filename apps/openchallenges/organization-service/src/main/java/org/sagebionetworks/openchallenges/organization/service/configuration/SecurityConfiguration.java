package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.sagebionetworks.openchallenges.organization.service.security.ApiKeyAuthenticationFilter;
import org.sagebionetworks.openchallenges.organization.service.security.JwtBearerAuthenticationFilter;
import org.sagebionetworks.openchallenges.organization.service.security.TrustedHeaderAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${openchallenges.security.mode:trusted-headers}")
  private String securityMode;

  @Bean
  public TrustedHeaderAuthenticationFilter trustedHeaderAuthenticationFilter() {
    return new TrustedHeaderAuthenticationFilter();
  }

  @Bean
  public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
    return new ApiKeyAuthenticationFilter();
  }

  @Bean
  public JwtBearerAuthenticationFilter jwtBearerAuthenticationFilter() {
    return new JwtBearerAuthenticationFilter();
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
          // Protected endpoints - require authentication
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/**")
          .authenticated()
          .requestMatchers(HttpMethod.POST, "/v1/organizations")
          .authenticated()
          .requestMatchers(HttpMethod.PUT, "/v1/organizations/**")
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, "/v1/organizations/*/participations/*/role/*")
          .authenticated()
          .requestMatchers(HttpMethod.POST, "/v1/organizations/*/participations")
          .authenticated()
          .anyRequest()
          .authenticated()
      );

    // Configure authentication filters based on security mode
    if ("trusted-headers".equals(securityMode)) {
      // API Gateway mode - trust headers from gateway
      http.addFilterBefore(trustedHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    } else if ("direct".equals(securityMode)) {
      // Direct mode - validate tokens/keys directly (placeholder implementations)
      http.addFilterBefore(jwtBearerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
      http.addFilterAfter(apiKeyAuthenticationFilter(), JwtBearerAuthenticationFilter.class);
    } else {
      // Default to trusted headers mode
      http.addFilterBefore(trustedHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    http.httpBasic(httpBasic -> {});

    return http.build();
  }
}
