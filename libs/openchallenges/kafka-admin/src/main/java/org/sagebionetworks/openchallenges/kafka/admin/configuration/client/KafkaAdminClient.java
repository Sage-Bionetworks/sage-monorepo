package org.sagebionetworks.openchallenges.kafka.admin.configuration.client;

import org.apache.kafka.clients.admin.AdminClient;
import org.sagebionetworks.openchallenges.kafka.admin.configuration.KafkaConfigurationData;
import org.sagebionetworks.openchallenges.kafka.admin.configuration.RetryConfigurationData;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAdminClient {

  private KafkaConfigurationData kafkaConfigurationData;

  private RetryConfigurationData retryConfigurationData;

  private AdminClient adminClient;

  private RetryTemplate retryTemplate;

  public KafkaAdminClient(
      KafkaConfigurationData configuration,
      RetryConfigurationData retryConfigurationData,
      AdminClient client,
      RetryTemplate template) {
    this.kafkaConfigurationData = configuration;
    this.retryConfigurationData = retryConfigurationData;
    this.adminClient = client;
    this.retryTemplate = template;
  }
}
