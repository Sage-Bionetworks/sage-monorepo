package org.sagebionetworks.openchallenges.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kaggle-to-kafka-service")
public class KaggleToKafkaServiceConfiguration {
  private List<String> kaggleSearchTerms;
  private String kaggleUsername;
  private String kaggleKey;
  private String welcomeMessage;
  private Boolean enableMockChallenges;
  private Long mockSleepMs;
  private Integer mockMinChallengeNameLength;
  private Integer mockMaxChallengeNameLength;
}
