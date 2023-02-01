package org.sagebionetworks.openchallenges.kafka.admin.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-configuration")
public class KafkaConfigurationData {
  private String bootstrapServers;
  private String schemaRegistryUrlKey;
  private String schemaRegistryUrl;
  private String topicName;
  private List<String> topicNamesToCreate;
  private Integer numOfPartitions;
  private Short replicationFactor;
}
