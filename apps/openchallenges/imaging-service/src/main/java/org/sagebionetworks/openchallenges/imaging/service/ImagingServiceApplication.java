package org.sagebionetworks.openchallenges.imaging.service;

import org.sagebionetworks.openchallenges.app.config.data.ImagingServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = {"org.sagebionetworks.openchallenges.imaging.service"})
public class ImagingServiceApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(ImagingServiceApplication.class);

  private final ImagingServiceConfigData imagingServiceConfigData;

  public ImagingServiceApplication(ImagingServiceConfigData imagingServiceConfigData) {
    this.imagingServiceConfigData = imagingServiceConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ImagingServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info(imagingServiceConfigData.getWelcomeMessage());
  }
}
