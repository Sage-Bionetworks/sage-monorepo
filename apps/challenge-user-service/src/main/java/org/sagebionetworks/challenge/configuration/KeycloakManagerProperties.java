package org.sagebionetworks.challenge.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.config.keycloak")
public class KeycloakManagerProperties {

  private String serverUrl;
  private String realm;
  private String clientId;
  private String clientSecret;

}
