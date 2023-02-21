package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.initializer;

import org.sagebionetworks.openchallenges.app.config.data.KafkaConfigData;
import org.sagebionetworks.openchallenges.kafka.admin.client.KafkaAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaSteamInitializer implements StreamInitializer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaSteamInitializer.class);

  private final KafkaConfigData kafkaConfigData;

  private final KafkaAdminClient kafkaAdminClient;

  public KafkaSteamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaAdminClient = kafkaAdminClient;
  }

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    LOG.info(
        "Topic(s) with name {} are ready for operation.", kafkaConfigData.getTopicNamesToCreate());
  }
}
