package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf()
      .disable()
      .cors()
      .disable()
      .authorizeRequests()
      .antMatchers("**")
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .httpBasic(); // Use Basic Auth for simplicity; replace as needed
    return http.build();
  }
}
