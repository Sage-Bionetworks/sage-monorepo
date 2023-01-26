package org.sagebionetworks.openchallenges.kafka.producer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-configuration")
public class KafkaProducerConfigurationData {
  private String keySerializerClass;
  private String valueSerializerClass;
  private String compressionType;
  private String acks;
  private Integer batchSize;
  private Integer batchSizeBoostFactor;
  private Integer lingerMs;
  private Integer requestTimeoutMs;
  private Integer retryCount;
}
