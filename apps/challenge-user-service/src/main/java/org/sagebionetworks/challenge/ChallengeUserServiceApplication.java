package org.sagebionetworks.challenge;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "org.sagebionetworks.challenge",
      "org.sagebionetworks.challenge.api",
      "org.sagebionetworks.challenge.configuration"
    })
public class ChallengeUserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeUserServiceApplication.class, args);
  }

  @Bean
  public Module jsonNullableModule() {
    return new JsonNullableModule();
  }
}
