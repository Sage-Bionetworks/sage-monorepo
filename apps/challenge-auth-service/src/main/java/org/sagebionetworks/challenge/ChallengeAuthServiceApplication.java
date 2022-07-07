package org.sagebionetworks.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ChallengeAuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeAuthServiceApplication.class, args);
  }

}
