package org.sagebionetworks.bixarena.auth.service.configuration;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.auth.service.security.SessionAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AppProperties appProperties;

  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http,
    SessionAuthenticationFilter sessionAuthenticationFilter
  ) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      // Populate SecurityContext from session (after OIDC callback) early in chain
      .addFilterBefore(sessionAuthenticationFilter, AnonymousAuthenticationFilter.class)
      .authorizeHttpRequests(authz ->
        authz
          .requestMatchers(
            "/.well-known/jwks.json",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/auth/login",
            "/auth/callback",
            "/oauth2/token",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
          )
          .permitAll()
          // All other requests require authentication (including /auth/logout)
          // Role-based authorization is handled by @PreAuthorize annotations
          .anyRequest()
          .authenticated()
      )
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(appProperties.cors().allowCredentials());
    config.setAllowedOrigins(appProperties.cors().allowedOrigins());
    config.setAllowedMethods(appProperties.cors().allowedMethods());
    config.setAllowedHeaders(appProperties.cors().allowedHeaders());
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
