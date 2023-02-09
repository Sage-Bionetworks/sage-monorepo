package org.sagebionetworks.openchallenges.service.registry;

import org.sagebionetworks.openchallenges.app.config.data.ServiceRegistryConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableEurekaServer
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ServiceRegistryApplication implements CommandLineRunner {

  private final ServiceRegistryConfigData serviceRegistryConfigData;

  public ServiceRegistryApplication(ServiceRegistryConfigData serviceRegistryConfigData) {
    this.serviceRegistryConfigData = serviceRegistryConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ServiceRegistryApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(serviceRegistryConfigData.getWelcomeMessage());
  }

}
