package org.sagebionetworks.openchallenges.organization.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(
    basePackages = {
      "org.sagebionetworks.openchallenges.organization.service",
      "org.sagebionetworks.openchallenges.organization.service.api",
      "org.sagebionetworks.openchallenges.organization.service.configuration"
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
