package org.sagebionetworks.openchallenges.runner;

import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.springframework.stereotype.Component;

@Component
public class KaggleKafkaStreamRunner implements StreamRunner {

  private final KaggleToKafkaServiceConfiguration config;

  public KaggleKafkaStreamRunner(KaggleToKafkaServiceConfiguration config) {
    this.config = config;
  }

  @Override
  public void start() {

  }
}
