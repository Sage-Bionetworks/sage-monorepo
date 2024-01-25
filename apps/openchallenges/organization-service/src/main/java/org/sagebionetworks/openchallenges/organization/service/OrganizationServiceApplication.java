package org.sagebionetworks.openchallenges.organization.service;

import org.sagebionetworks.openchallenges.app.config.data.OrganizationServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

// XXX to trigger Sonar scan
@ComponentScan(basePackages = {"org.sagebionetworks.openchallenges"})
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class OrganizationServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceApplication.class);

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
    LOG.info(organizationServiceConfigData.getWelcomeMessage());
  }
}
