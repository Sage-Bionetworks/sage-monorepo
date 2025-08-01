package org.sagebionetworks.openchallenges.challenge.service.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganizationServiceClientConfiguration {

  @Value("${openchallenges-challenge-service.organization-service.api-key}")
  private String apiKey;

  @Bean
  public RequestInterceptor bearerAuthRequestInterceptor() {
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate template) {
        if (apiKey != null && !apiKey.isEmpty()) {
          template.header("Authorization", "Bearer " + apiKey);
        }
      }
    };
  }
}
