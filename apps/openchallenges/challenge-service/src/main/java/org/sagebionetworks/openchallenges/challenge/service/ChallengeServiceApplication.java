package org.sagebionetworks.openchallenges.challenge.service;

import org.sagebionetworks.openchallenges.app.config.data.ChallengeServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.sagebionetworks.openchallenges" })
@EnableFeignClients
@SpringBootApplication
public class ChallengeServiceApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeServiceApplication.class);

  private final ChallengeServiceConfigData challengeServiceConfigData;

  public ChallengeServiceApplication(ChallengeServiceConfigData challengeServiceConfigData) {
    this.challengeServiceConfigData = challengeServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ChallengeServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(challengeServiceConfigData.getWelcomeMessage());
  }
}
