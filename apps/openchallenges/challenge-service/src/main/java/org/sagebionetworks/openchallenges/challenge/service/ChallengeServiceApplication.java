package org.sagebionetworks.openchallenges.challenge.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(
    basePackages = {
      "org.sagebionetworks.openchallenges.challenge.service"
    })
public class ChallengeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeServiceApplication.class, args);
  }

  // @Bean
  // public Module jsonNullableModule() {
  //   return new JsonNullableModule();
  // }
}
