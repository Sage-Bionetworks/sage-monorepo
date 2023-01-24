package org.sagebionetworks.openchallenges.kafka.producer.configuration.service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.sagebionetworks.openchallenges.kafka.model.Cat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
public class KaggleKafkaProducer implements KafkaProducer<Long, Cat> {

  private KafkaTemplate<Long, Cat> kafkaTemplate;

  public KaggleKafkaProducer(KafkaTemplate<Long, Cat> template) {
    this.kafkaTemplate = template;
  }

  public void send(String topicName, Long key, Cat message) {
    log.info("Sending message='{}' to topic={}", message, topicName);
    ListenableFuture<SendResult<Long, Cat>> kafkaResultFuture =
        kafkaTemplate.send(topicName, key, message);
    addCallback(topicName, message, kafkaResultFuture);
  }

  @PreDestroy
  public void close() {
    if (kafkaTemplate != null) {
      log.info("Closing Kafka producer.");
      kafkaTemplate.destroy();
    }
  }

  private void addCallback(
      String topicName, Cat message, ListenableFuture<SendResult<Long, Cat>> kafkaResultFuture) {
    kafkaResultFuture.addCallback(
        new ListenableFutureCallback<>() {
          @Override
          public void onFailure(Throwable throwable) {
            log.error("Error while sending message {} to topic {}.", message.toString(), topicName);
          }

          @Override
          public void onSuccess(SendResult<Long, Cat> result) {
            RecordMetadata metadata = result.getRecordMetadata();
            log.debug(
                "Received new metadata. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}, at time {}",
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                metadata.timestamp(),
                System.nanoTime());
          }
        });
  }
}
