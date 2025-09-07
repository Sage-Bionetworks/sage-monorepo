package org.sagebionetworks.openchallenges.challenge.service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OAuth2 client credentials.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "openchallenges-challenge-service.oauth2.client")
public class OAuth2Properties {

  /**
   * The OAuth2 client ID for service-to-service authentication.
   */
  private String clientId;

  /**
   * The OAuth2 client secret for service-to-service authentication.
   */
  private String clientSecret;
}
