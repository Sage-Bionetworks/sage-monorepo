package org.sagebionetworks.amp.als.dataset.service;

// TODO: next line
// import org.sagebionetworks.openchallenges.app.config.data.ChallengeServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sagebionetworks.amp.als" })
public class DatasetServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceApplication.class);

  // private final ChallengeServiceConfigData challengeServiceConfigData;

  // public DatasetServiceApplication(ChallengeServiceConfigData challengeServiceConfigData) {
  //   this.challengeServiceConfigData = challengeServiceConfigData;
  // }

  public DatasetServiceApplication() {}

  public static void main(String[] args) {
    SpringApplication.run(DatasetServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // LOG.info(challengeServiceConfigData.getWelcomeMessage());
  }
}
