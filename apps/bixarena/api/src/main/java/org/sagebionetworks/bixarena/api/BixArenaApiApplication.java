package org.sagebionetworks.bixarena.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class BixArenaApiApplication implements CommandLineRunner {

  private final AppProperties appProperties;

  public static void main(String[] args) {
    SpringApplication.run(BixArenaApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(appProperties.welcomeMessage());
  }
}
