package org.sagebionetworks.openchallenges.kafka.producer.configuration.service;

import java.io.Serializable;
import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
  void send(String topicName, K key, V message);
}
