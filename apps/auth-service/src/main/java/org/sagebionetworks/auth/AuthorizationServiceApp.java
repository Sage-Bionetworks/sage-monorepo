package org.sagebionetworks.auth;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.auth.config.KeycloakServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({KeycloakServerProperties.class})
public class AuthorizationServiceApp {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(AuthorizationServiceApp.class, args);
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(
      ServerProperties serverProperties, KeycloakServerProperties keycloakServerProperties) {

    return (evt) -> {
      Integer port = serverProperties.getPort();
      String keycloakContextPath = keycloakServerProperties.getContextPath();

      log.info("Embedded Keycloak started: http://localhost:{}{}", port, keycloakContextPath);
    };
  }
}
