package org.sagebionetworks.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Challenge API Gateway.
 */
@EnableEurekaClient
@SpringBootApplication
public class ChallengeApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeApiGatewayApplication.class, args);
  }

}
