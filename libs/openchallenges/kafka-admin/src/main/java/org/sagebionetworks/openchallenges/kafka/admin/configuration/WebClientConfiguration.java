package org.sagebionetworks.openchallenges.kafka.admin.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }
}
