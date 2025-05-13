package org.sagebionetworks.openchallenges.mcp.server.config;

import org.sagebionetworks.openchallenges.api.client.ApiClient;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenChallengesApiClientConfig {

  private final ApiClient apiClient;

  public OpenChallengesApiClientConfig() {
    this.apiClient = new ApiClient();
    this.apiClient.setBasePath("http://openchallenges-api-gateway:8082/api/v1");
  }

  @Bean
  public ChallengeAnalyticsApi challengeAnalyticsApi() {
    return new ChallengeAnalyticsApi(apiClient);
  }
}
