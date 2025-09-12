package org.sagebionetworks.openchallenges.auth.service.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.auth.service.security.OAuth2WebAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AppProperties authServiceProperties;

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
   * Configure security filter chain for OAuth2 Resource Server and web authentication.
   *
   * This filter chain handles:
   * 1. OAuth2 Resource Server functionality (validating JWTs for API endpoints)
   * 2. Web authentication for profile management pages
   * 3. Public endpoints (health checks, documentation, OAuth2 flows, etc.)
   *
   * Security approach:
   * - Public endpoints are explicitly defined and allow anonymous access
   * - All other endpoints require authentication (JWT for APIs, web auth for web pages)
   * - Authentication type is determined by the OAuth2WebAuthenticationFilter and Spring Security filters
   *
   * Note: OAuth2 Authorization Server endpoints (token issuance, authorization)
   * are handled by a separate SecurityFilterChain in OAuth2AuthorizationServerConfiguration.
   *
   * API key authentication is handled by the API Gateway, which converts
   * API keys to JWTs before forwarding requests to this service.
   */
  @Bean
  @Order(2)
  public SecurityFilterChain filterChain(
    HttpSecurity http,
    OAuth2WebAuthenticationFilter oAuth2WebAuthenticationFilter,
    org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder
  ) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for API
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless for API
      .logout(logout -> logout.disable()) // Disable Spring Security's default logout
      // Configure OAuth2 Resource Server for JWT validation on API endpoints
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
      .authorizeHttpRequests(authz ->
        authz
          // Public endpoints - no authentication required
          .requestMatchers(authServiceProperties.getApi().getPublicEndpoints())
          .permitAll()
          // All other endpoints require authentication
          .anyRequest()
          .authenticated()
      )
      // Add OAuth2 web authentication filter only
      .addFilterBefore(oAuth2WebAuthenticationFilter, BearerTokenAuthenticationFilter.class) // OAuth2 web filter
      // Configure exception handling for web authentication
      .exceptionHandling(exceptions ->
        exceptions.authenticationEntryPoint((request, response, authException) -> {
          String requestUri = request.getRequestURI();
          // For web interface endpoints, redirect to login page
          if (requestUri.startsWith("/profile") || requestUri.equals("/")) {
            response.sendRedirect("/login");
          } else {
            // For API endpoints, return 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response
              .getWriter()
              .write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
          }
        })
      );

    return http.build();
  }
}
