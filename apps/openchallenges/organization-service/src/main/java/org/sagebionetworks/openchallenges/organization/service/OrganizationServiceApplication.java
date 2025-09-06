package org.sagebionetworks.openchallenges.organization.service;

import org.sagebionetworks.openchallenges.app.config.data.OrganizationServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan(basePackages = { "org.sagebionetworks.openchallenges" })
@EnableFeignClients
@SpringBootApplication
@RequiredArgsConstructor
public class OrganizationServiceApplication implements CommandLineRunner {

  private final OrganizationServiceConfigData organizationServiceConfigData;

  public static void main(String[] args) {
    SpringApplication.run(OrganizationServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(organizationServiceConfigData.getWelcomeMessage());
  }
}
