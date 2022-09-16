package org.sagebionetworks.challenge.configuration;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
// import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
// import org.sagebionetworks.challenge.exception.CustomKeycloakAuthenticationHandler;
// import org.sagebionetworks.challenge.exception.RestAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

  // @Autowired
  // RestAccessDeniedHandler restAccessDeniedHandler;

  // @Autowired
  // CustomKeycloakAuthenticationHandler customKeycloakAuthenticationHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf().disable().cors().disable().authorizeRequests()
        .antMatchers("/api/v1/users/register", "/api/v1/api-docs/**", "/v3/**", "/api/v1/**")
        .permitAll().anyRequest().authenticated();

    // Custom error handler
    // http.exceptionHandling().accessDeniedHandler(restAccessDeniedHandler);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  // // Keycloak auth exception handler
  // @Bean
  // @Override
  // protected KeycloakAuthenticationProcessingFilter
  // keycloakAuthenticationProcessingFilter()
  // throws Exception {
  // KeycloakAuthenticationProcessingFilter filter =
  // new KeycloakAuthenticationProcessingFilter(this.authenticationManagerBean());
  // filter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy());
  // filter.setAuthenticationFailureHandler(customKeycloakAuthenticationHandler);
  // return filter;
  // }

}
