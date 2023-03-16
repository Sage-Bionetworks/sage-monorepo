package org.sagebionetworks.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = {"org.sagebionetworks.openchallenges"})
public class ChallengeUserServiceApplication implements CommandLineRunner  {

  private static final Logger LOG = LoggerFactory.getLogger(ChallengeUserServiceApplication.class);

  // private final ChallengeerviceConfigData organizationServiceConfigData;

  public static void main(String[] args) {
    SpringApplication.run(ChallengeUserServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("Hello");
    // LOG.info(organizationServiceConfigData.getWelcomeMessage());
  }
}
