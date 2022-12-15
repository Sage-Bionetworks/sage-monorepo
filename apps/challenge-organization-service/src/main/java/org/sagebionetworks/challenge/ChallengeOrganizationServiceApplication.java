package org.sagebionetworks.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(
    basePackages = {
      "org.sagebionetworks.challenge",
      "org.sagebionetworks.challenge.api",
      "org.sagebionetworks.challenge.configuration"
    })
public class ChallengeOrganizationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeOrganizationServiceApplication.class, args);
  }

  // @Bean
  // public Module jsonNullableModule() {
  //   return new JsonNullableModule();
  // }
}
