package org.sagebionetworks.openchallenges.api.gateway;

import org.sagebionetworks.openchallenges.app.config.data.ApiGatewayConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ApiGatewayApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

  private final ApiGatewayConfigData apiGatewayConfigData;

  public ApiGatewayApplication(ApiGatewayConfigData apiGatewayConfigData) {
    this.apiGatewayConfigData = apiGatewayConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(apiGatewayConfigData.getWelcomeMessage());
  }
}
