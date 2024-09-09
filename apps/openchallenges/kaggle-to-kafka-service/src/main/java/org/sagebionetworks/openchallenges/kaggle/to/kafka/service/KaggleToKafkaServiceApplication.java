package org.sagebionetworks.openchallenges.kaggle.to.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.KaggleToKafkaServiceConfigData;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.initializer.StreamInitializer;
import org.sagebionetworks.openchallenges.kaggle.to.kafka.service.runner.StreamRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class KaggleToKafkaServiceApplication implements CommandLineRunner {

  private final KaggleToKafkaServiceConfigData kaggleToKafkaServiceConfigData;

  private final StreamRunner streamRunner;

  private final StreamInitializer streamInitializer;

  public KaggleToKafkaServiceApplication(
    KaggleToKafkaServiceConfigData kaggleToKafkaServiceConfigData,
    StreamRunner streamRunner,
    StreamInitializer streamInitializer
  ) {
    this.kaggleToKafkaServiceConfigData = kaggleToKafkaServiceConfigData;
    this.streamRunner = streamRunner;
    this.streamInitializer = streamInitializer;
  }

  public static void main(String[] args) {
    SpringApplication.run(KaggleToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(kaggleToKafkaServiceConfigData.getWelcomeMessage());
    log.info("{}", kaggleToKafkaServiceConfigData.getKaggleSearchTerms());
    streamInitializer.init();
    streamRunner.start();
  }
}
