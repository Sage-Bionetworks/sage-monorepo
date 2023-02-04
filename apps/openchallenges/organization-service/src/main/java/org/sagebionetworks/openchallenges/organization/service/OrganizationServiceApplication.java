package org.sagebionetworks.openchallenges.organization.service;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.OrganizationServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = {"org.sagebionetworks.openchallenges"})
public class OrganizationServiceApplication implements CommandLineRunner {

  private final OrganizationServiceConfigData organizationServiceConfigData;

  public OrganizationServiceApplication(
      OrganizationServiceConfigData organizationServiceConfigData) {
    this.organizationServiceConfigData = organizationServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(OrganizationServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(organizationServiceConfigData.getWelcomeMessage());
  }
}
