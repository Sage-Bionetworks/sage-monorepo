package org.sagebionetworks.challenge.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.config.keycloak")
public class KeycloakManagerProperties {

  private String serverUrl;
  private String realm;
  private String clientId;
  private String clientSecret;
}
