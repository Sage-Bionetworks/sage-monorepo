package org.sagebionetworks.openchallenges.api.gateway;

import org.sagebionetworks.openchallenges.app.config.data.ApiGatewayConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ApiGatewayApplication implements CommandLineRunner {

  private final ApiGatewayConfigData apiGatewayConfigData;

  public ApiGatewayApplication(ApiGatewayConfigData apiGatewayConfigData) {
    this.apiGatewayConfigData = apiGatewayConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(apiGatewayConfigData.getWelcomeMessage());
  }
}
