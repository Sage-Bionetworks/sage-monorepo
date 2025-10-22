package org.sagebionetworks.bixarena.api.configuration;

import java.util.List;
import org.sagebionetworks.bixarena.api.security.JwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private JwtAuthenticationConverter jwtAuthenticationConverter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(authz ->
        authz
          .requestMatchers(
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v1/example-prompts",
            "/v1/example-prompts/**",
            "/v1/leaderboards",
            "/v1/leaderboards/**",
            "/v1/models",
            "/v1/models/**",
            "/v3/api-docs/**"
          )
          .permitAll()
          .requestMatchers("/v1/admin/**")
          .hasRole("ADMIN")
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(oauth2 ->
        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
      )
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    String jwksUri = appProperties.authService().baseUrl() + "/.well-known/jwks.json";
    return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
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
