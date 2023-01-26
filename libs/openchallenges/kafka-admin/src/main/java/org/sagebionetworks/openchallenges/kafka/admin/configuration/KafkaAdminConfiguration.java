package org.sagebionetworks.openchallenges.kafka.admin.configuration;

import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
public class KafkaAdminConfiguration {

  private final KafkaConfigurationData kafkaConfigurationData;

  public KafkaAdminConfiguration(KafkaConfigurationData configurationData) {
    this.kafkaConfigurationData = configurationData;
  }

  @Bean
  public AdminClient adminClient() {
    return AdminClient.create(
        Map.of(
            CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
            kafkaConfigurationData.getBootstrapServers()));
  }
}
