package org.sagebionetworks.openchallenges.challenge.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for HTTP clients used by the challenge service.
 */
@Configuration
public class RestClientConfiguration {

  /**
   * RestClient bean for making HTTP requests to other services.
   * RestClient is the modern replacement for RestTemplate in Spring Boot 3.2+.
   */
  @Bean
  public RestClient restClient() {
    return RestClient.builder()
      .build();
  }
}
