package org.sagebionetworks.openchallenges.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kaggle-to-kafka-service")
public class KaggleToKafkaServiceConfiguration {
  private List<String> kaggleSearchTerms;
}
