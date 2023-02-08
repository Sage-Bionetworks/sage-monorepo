package org.sagebionetworks.openchallenges.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "challenge-to-elasticsearch-service")
public class ChallengeToElasticsearchServiceConfigData {

  private String welcomeMessage;
}
