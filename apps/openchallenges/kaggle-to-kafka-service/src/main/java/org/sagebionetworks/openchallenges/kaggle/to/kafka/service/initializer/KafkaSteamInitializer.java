package org.sagebionetworks.openchallenges.kaggle.to.kafka.service.initializer;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.KafkaConfigData;
import org.sagebionetworks.openchallenges.kafka.admin.client.KafkaAdminClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaSteamInitializer implements StreamInitializer {

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
    log.info(
      "Topic(s) with name {} are ready for operation.",
      kafkaConfigData.getTopicNamesToCreate()
    );
  }
}
