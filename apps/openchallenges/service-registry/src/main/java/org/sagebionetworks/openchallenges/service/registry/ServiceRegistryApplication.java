package org.sagebionetworks.openchallenges.service.registry;

import org.sagebionetworks.openchallenges.app.config.data.ServiceRegistryConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaServer
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ServiceRegistryApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryApplication.class);

  private final ServiceRegistryConfigData serviceRegistryConfigData;

  public ServiceRegistryApplication(ServiceRegistryConfigData serviceRegistryConfigData) {
    this.serviceRegistryConfigData = serviceRegistryConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ServiceRegistryApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(serviceRegistryConfigData.getWelcomeMessage());
  }
}
