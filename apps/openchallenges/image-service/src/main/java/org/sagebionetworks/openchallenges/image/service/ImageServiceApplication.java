package org.sagebionetworks.openchallenges.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.sagebionetworks.openchallenges" })
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class ImageServiceApplication implements CommandLineRunner {

  private final ImageServiceConfigData imageServiceConfigData;

  public static void main(String[] args) {
    SpringApplication.run(ImageServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(imageServiceConfigData.getWelcomeMessage());
  }
}
