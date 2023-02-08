package org.sagebionetworks.openchallenges.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openchallenges-retry")
public class RetryConfigData {

  private Long initialIntervalMs;
  private Long maxIntervalMs;
  private Double multiplier;
  private Integer maxAttempts;
  private Long sleepTimeMs;
}
