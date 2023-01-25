package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.initializer;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.kafka.admin.client.KafkaAdminClient;
import org.sagebionetworks.openchallenges.kafka.admin.configuration.KafkaConfigurationData;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaSteamInitializer implements StreamInitializer {

  private final KafkaConfigurationData kafkaConfigurationData;

  private final KafkaAdminClient kafkaAdminClient;

  public KafkaSteamInitializer(
      KafkaConfigurationData kafkaConfigurationData, KafkaAdminClient kafkaAdminClient) {
    this.kafkaConfigurationData = kafkaConfigurationData;
    this.kafkaAdminClient = kafkaAdminClient;
  }

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    log.info(
        "Topic(s) with name {} are ready for operation.",
        kafkaConfigurationData.getTopicNamesToCreate());
  }
}
