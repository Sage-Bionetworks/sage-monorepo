package org.sagebionetworks.openchallenges.app.config.data;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kaggle-to-kafka-service")
public class KaggleToKafkaServiceConfigData {
  private List<String> kaggleSearchTerms;
  private String kaggleUsername;
  private String kaggleKey;
  private String kaggleBaseUrl;
  private String welcomeMessage;
  private Boolean enableMockChallenges;
  private Long mockSleepMs;
  private Integer mockMinChallengeNameLength;
  private Integer mockMaxChallengeNameLength;
}
