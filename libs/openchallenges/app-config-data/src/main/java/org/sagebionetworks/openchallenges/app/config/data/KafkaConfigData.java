package org.sagebionetworks.openchallenges.app.config.data;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
  private String bootstrapServers;
  private String schemaRegistryUrlKey;
  private String schemaRegistryUrl;
  private String topicName;
  private List<String> topicNamesToCreate;
  private Integer numOfPartitions;
  private Short replicationFactor;
}
