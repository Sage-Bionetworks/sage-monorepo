package org.sagebionetworks.openchallenges.kafka.admin.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-configuration")
public class RetryConfigurationData {

  private Long initialIntervalMs;
  private Long maxIntervalMs;
  private Double multiplier;
  private Integer maxAttempts;
  private Long sleepTimeMs;
}
