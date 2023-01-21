package org.sagebionetworks.openchallenges;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.sagebionetworks.openchallenges.runner.StreamRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class KaggleToKafkaServiceApplication implements CommandLineRunner {

  private final KaggleToKafkaServiceConfiguration config;

  private final StreamRunner streamRunner;

  public KaggleToKafkaServiceApplication(
      KaggleToKafkaServiceConfiguration kaggleToKafkaServiceConfiguration, StreamRunner streamRunner) {
    this.config = kaggleToKafkaServiceConfiguration;
    this.streamRunner = streamRunner;
  }

  public static void main(String[] args) {
    SpringApplication.run(KaggleToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(this.config.getWelcomeMessage());
    log.info("{}", this.config.getKaggleSearchTerms());
    this.streamRunner.start();
  }
}
