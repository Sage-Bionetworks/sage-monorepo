package org.sagebionetworks.openchallenges;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.configuration.KaggleToKafkaServiceConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KaggleToKafkaServiceApplication implements CommandLineRunner {

  private final KaggleToKafkaServiceConfiguration config;

  public KaggleToKafkaServiceApplication(
      KaggleToKafkaServiceConfiguration kaggleToKafkaServiceConfiguration) {
    this.config = kaggleToKafkaServiceConfiguration;
  }

  public static void main(String[] args) {
    SpringApplication.run(KaggleToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(this.config.getWelcomeMessage());
    log.info("{}", this.config.getKaggleSearchTerms());
  }
}
