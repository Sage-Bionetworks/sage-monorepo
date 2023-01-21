package org.sagebionetworks.openchallenges.runner;

import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "kaggle-to-kafka-service.enable-mock-challenges",
    havingValue = "false",
    matchIfMissing = true)
public class KaggleKafkaStreamRunner implements StreamRunner {

  private final KaggleToKafkaServiceConfiguration config;

  public KaggleKafkaStreamRunner(KaggleToKafkaServiceConfiguration config) {
    this.config = config;
  }

  @Override
  public void start() {}
}
