package org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service.consumer;

import java.util.List;
import java.util.Objects;
import org.sagebionetworks.openchallenges.app.config.data.KafkaConfigData;
import org.sagebionetworks.openchallenges.app.config.data.KafkaConsumerConfigData;
import org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service.transformer.AvroToElasticsearchModelTransformer;
import org.sagebionetworks.openchallenges.elasticsearch.index.client.service.ElasticsearchIndexClient;
import org.sagebionetworks.openchallenges.elasticsearch.model.index.ChallengeIndexModel;
import org.sagebionetworks.openchallenges.kafka.admin.client.KafkaAdminClient;
import org.sagebionetworks.openchallenges.kafka.model.KaggleCompetitionAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ChallengeKafkaConsumer implements KafkaConsumer<Long, KaggleCompetitionAvroModel> {

  private static final Logger LOG = LoggerFactory.getLogger(ChallengeKafkaConsumer.class);

  private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  private final KafkaAdminClient kafkaAdminClient;

  private final KafkaConfigData kafkaConfigData;

  private final KafkaConsumerConfigData kafkaConsumerConfigData;

  private final AvroToElasticsearchModelTransformer avroToElasticsearchModelTransformer;

  private final ElasticsearchIndexClient<ChallengeIndexModel> elasticsearchIndexClient;

  public ChallengeKafkaConsumer(
      KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
      KafkaAdminClient kafkaAdminClient,
      KafkaConfigData kafkaConfigData,
      KafkaConsumerConfigData kafkaConsumerConfigData,
      AvroToElasticsearchModelTransformer avroToElasticsearchModelTransformer,
      ElasticsearchIndexClient<ChallengeIndexModel> elasticsearchIndexClient) {
    this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    this.kafkaAdminClient = kafkaAdminClient;
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaConsumerConfigData = kafkaConsumerConfigData;
    this.avroToElasticsearchModelTransformer = avroToElasticsearchModelTransformer;
    this.elasticsearchIndexClient = elasticsearchIndexClient;
  }

  @EventListener
  public void onAppStarted(ApplicationStartedEvent event) {
    kafkaAdminClient.checkTopicsCreated();
    LOG.info(
        "Topics with name {} is ready for operations.",
        kafkaConfigData.getTopicNamesToCreate().toArray());
    Objects.requireNonNull(
            kafkaListenerEndpointRegistry.getListenerContainer(
                kafkaConsumerConfigData.getConsumerGroupId()))
        .start();
  }

  // ${openchallenges-kafka.topic-name}
  @Override
  @KafkaListener(
      id = "${openchallenges-kafka-consumer.consumer-group-id}",
      topics = "${openchallenges-kafka.topic-name}")
  public void receive(
      @Payload List<KaggleCompetitionAvroModel> messages,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    LOG.info(
        "{} number of messages received with keys {}, partitions {} and offsets {}, "
            + "sending them to elasticsearch: Thread id {}",
        messages.size(),
        keys.toString(),
        partitions.toString(),
        offsets.toString(),
        Thread.currentThread().getId());
    List<ChallengeIndexModel> elasticsearchModels =
        avroToElasticsearchModelTransformer.getElasticsearchModels(messages);
    List<String> documentIds = elasticsearchIndexClient.save(elasticsearchModels);
    LOG.info("Documents saved to Elasticsearch with ids {}", documentIds.toArray());
  }
}
