package org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.ChallengeToElasticsearchServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ChallengeToElasticsearchServiceApplication implements CommandLineRunner {

  private final ChallengeToElasticsearchServiceConfigData challengeToElasticsearchServiceConfigData;

  public ChallengeToElasticsearchServiceApplication(
    ChallengeToElasticsearchServiceConfigData challengeToElasticsearchServiceConfigData
  ) {
    this.challengeToElasticsearchServiceConfigData = challengeToElasticsearchServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ChallengeToElasticsearchServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(challengeToElasticsearchServiceConfigData.getWelcomeMessage());
  }
}
