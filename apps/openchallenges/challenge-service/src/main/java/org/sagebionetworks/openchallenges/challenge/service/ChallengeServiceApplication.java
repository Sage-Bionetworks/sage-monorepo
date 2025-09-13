package org.sagebionetworks.openchallenges.challenge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.ChallengeServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.sagebionetworks.openchallenges" })
@EnableFeignClients
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class ChallengeServiceApplication implements CommandLineRunner {

  private final ChallengeServiceConfigData challengeServiceConfigData;

  public static void main(String[] args) {
    SpringApplication.run(ChallengeServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(challengeServiceConfigData.getWelcomeMessage());
  }
}
