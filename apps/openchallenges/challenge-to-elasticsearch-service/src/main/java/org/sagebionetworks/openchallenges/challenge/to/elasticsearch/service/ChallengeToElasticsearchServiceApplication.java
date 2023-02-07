package org.sagebionetworks.openchallenges.challenge.to.elasticsearch.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ChallengeToElasticsearchServiceApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeToElasticsearchServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // TODO Auto-generated method stub

  }
}
