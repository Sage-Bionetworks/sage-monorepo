package org.sagebionetworks.bixarena.auth.service.configuration;

import java.util.List;
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
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

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
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/auth/**",
            "/auth/logout",
            "/v3/api-docs/**"
          )
          .permitAll()
          .requestMatchers("/v1/admin/**")
          .hasRole("ADMIN")
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
    config.setAllowCredentials(true);
    config.setAllowedOrigins(
      List.of(
        "http://localhost:8100",
        "http://127.0.0.1:8100",
        "http://localhost:7860",
        "http://127.0.0.1:7860"
      )
    );
    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
