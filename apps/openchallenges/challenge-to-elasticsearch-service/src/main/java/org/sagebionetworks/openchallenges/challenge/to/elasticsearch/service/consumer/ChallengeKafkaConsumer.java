package org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service.consumer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.KafkaConfigData;
import org.sagebionetworks.openchallenges.kafka.admin.client.KafkaAdminClient;
import org.sagebionetworks.openchallenges.kafka.model.KaggleCompetitionAvroModel;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChallengeKafkaConsumer implements KafkaConsumer<Long, KaggleCompetitionAvroModel> {

  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  private final KafkaAdminClient kafkaAdminClient;

  private final KafkaConfigData kafkaConfigData;

  public ChallengeKafkaConsumer(
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
    KafkaAdminClient kafkaAdminClient,
    KafkaConfigData kafkaConfigData
  ) {
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    log.info(
      "Topics with name {} is ready for operations.",
      kafkaConfigData.getTopicNamesToCreate().toArray()
    );
    kafkaListenerEndpointRegistry.getListenerContainer("challengeTopicListener").start();
  }

  // ${openchallenges-kafka.topic-name}
  @Override
  @KafkaListener(id = "challengeTopicListener", topics = "kaggle-topic")
  public void receive(
    @Payload List<KaggleCompetitionAvroModel> messages,
    @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets
  ) {
    log.info(
      "{} number of messages received with keys {}, partitions {} and offsets {}, " +
      "sending them to elasticsearch: Thread id {}",
      messages.size(),
      keys.toString(),
      partitions.toString(),
      offsets.toString(),
      Thread.currentThread().getId()
    );
  }
}
