package org.sagebionetworks.openchallenges.image.service;

import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = { "org.sagebionetworks.openchallenges" })
public class ImageServiceApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ImageServiceApplication.class);

  private final ImageServiceConfigData imageServiceConfigData;

  public ImageServiceApplication(ImageServiceConfigData imageServiceConfigData) {
    this.imageServiceConfigData = imageServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ImageServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(imageServiceConfigData.getWelcomeMessage());
  }
}
