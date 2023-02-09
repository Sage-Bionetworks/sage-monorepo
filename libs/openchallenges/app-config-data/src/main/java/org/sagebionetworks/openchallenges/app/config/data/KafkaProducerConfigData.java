package org.sagebionetworks.openchallenges.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openchallenges-kafka-producer")
public class KafkaProducerConfigData {

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
