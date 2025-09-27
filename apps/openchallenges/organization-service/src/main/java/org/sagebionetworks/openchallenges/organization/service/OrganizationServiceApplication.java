package org.sagebionetworks.openchallenges.organization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.organization.service.configuration.AppProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ConfigurationPropertiesScan
@EnableFeignClients
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class OrganizationServiceApplication implements CommandLineRunner {

  private final AppProperties appProperties;

  public static void main(String[] args) {
    SpringApplication.run(OrganizationServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(appProperties.welcomeMessage());
  }
}
