package org.sagebionetworks.openchallenges.auth.service.configuration;

import org.sagebionetworks.openchallenges.auth.service.security.ApiKeyAuthenticationFilter;
import org.sagebionetworks.openchallenges.auth.service.security.OAuth2WebAuthenticationFilter;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  /**
   * Default password encoder bean - supports both BCrypt and {noop} prefixes
   * This will be used by OAuth2 Authorization Server for client authentication
   * and can handle both user passwords (BCrypt) and OAuth2 client secrets ({noop})
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * Configure security filter chain with API key and OAuth2 web authentication
   */
  @Bean
  @Order(2)
  public SecurityFilterChain filterChain(
    HttpSecurity http,
    ApiKeyService apiKeyService,
    OAuth2WebAuthenticationFilter oAuth2WebAuthenticationFilter
  ) throws Exception {
    // Create the API key authentication filter here to avoid circular dependency
    ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
      apiKeyService
    );

    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for API
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless for API
      .logout(logout -> logout.disable()) // Disable Spring Security's default logout
      .authorizeHttpRequests(
        authz ->
          authz
            // OAuth2 Authorization Server endpoints
            .requestMatchers("/oauth2/**")
            .permitAll() // Standard OAuth2 endpoints (authorization, token, etc.)
            .requestMatchers("/.well-known/oauth-authorization-server")
            .permitAll() // OAuth2 Authorization Server discovery endpoint
            .requestMatchers("/.well-known/openid_configuration")
            .permitAll() // OpenID Connect discovery endpoint
            .requestMatchers("/.well-known/**")
            .permitAll() // Other well-known endpoints
            // Custom API endpoints (v1)
            .requestMatchers("/v1/auth/api-keys/validate")
            .permitAll() // API key validation (internal endpoint)
            .requestMatchers("/v1/**")
            .authenticated() // All other v1 API endpoints require authentication
            // Web interface endpoints for OAuth2
            .requestMatchers("/login", "/auth/oauth2/google", "/auth/callback")
            .permitAll() // OAuth2 web interface endpoints and login page
            .requestMatchers("/profile", "/profile/**")
            .permitAll() // Profile page and API key management endpoints accessible without JWT authentication
            .requestMatchers("/logout", "/logout/**")
            .permitAll() // Logout web interface endpoints
            .requestMatchers("/error")
            .permitAll() // Error page
            // Actuator endpoints
            .requestMatchers("/actuator/health", "/actuator/info")
            .permitAll() // Health checks
            // OpenAPI documentation
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .permitAll() // OpenAPI docs
            .anyRequest()
            .authenticated() // All other endpoints require authentication
      )
      // Add filters in order: OAuth2 web first, then API Key
      .addFilterBefore(oAuth2WebAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // OAuth2 web filter
      .addFilterAfter(apiKeyAuthenticationFilter, OAuth2WebAuthenticationFilter.class); // API key filter after OAuth2 web

    return http.build();
  }
}
