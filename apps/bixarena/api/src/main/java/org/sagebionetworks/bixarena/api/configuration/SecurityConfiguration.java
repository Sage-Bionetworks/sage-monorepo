package org.sagebionetworks.bixarena.api.configuration;

import java.util.List;
import org.sagebionetworks.bixarena.api.security.JwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
@EnableMethodSecurity(prePostEnabled = true)
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
          .requestMatchers(HttpMethod.POST, "/v1/battles")
          .permitAll()
          .requestMatchers(HttpMethod.PATCH, "/v1/battles/**")
          .permitAll()
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
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

    // Create validators for issuer and audience
    var issuerValidator = org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer(
      appProperties.jwt().expectedIssuer()
    );

    // Custom audience validator - only accept urn:bixarena:api
    org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> audienceValidator =
      token -> {
        var audiences = token.getAudience();
        if (audiences != null && audiences.contains(appProperties.jwt().expectedAudience())) {
          return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
        }
        return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
          new org.springframework.security.oauth2.core.OAuth2Error(
            "invalid_token",
            "Token audience must be " + appProperties.jwt().expectedAudience(),
            null
          )
        );
      };

    // Combine validators
    var validators = new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
      issuerValidator,
      audienceValidator
    );

    decoder.setJwtValidator(validators);

    return decoder;
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
    config.setAllowedMethods(List.of("GET", "POST", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
