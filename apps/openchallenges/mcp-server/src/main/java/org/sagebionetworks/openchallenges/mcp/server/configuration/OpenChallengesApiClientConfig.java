package org.sagebionetworks.openchallenges.mcp.server.configuration;

import org.sagebionetworks.openchallenges.api.client.ApiClient;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeApi;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeContributionApi;
import org.sagebionetworks.openchallenges.api.client.api.ChallengePlatformApi;
import org.sagebionetworks.openchallenges.api.client.api.EdamConceptApi;
import org.sagebionetworks.openchallenges.api.client.api.OrganizationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenChallengesApiClientConfig {

  private final ApiClient apiClient;

  public OpenChallengesApiClientConfig(AppProperties appProperties) {
    this.apiClient = new ApiClient();
    this.apiClient.setBasePath(appProperties.apiBaseUrl());
  }

  @Bean
  public ChallengeApi challengeApi() {
    return new ChallengeApi(apiClient);
  }

  @Bean
  public ChallengeAnalyticsApi challengeAnalyticsApi() {
    return new ChallengeAnalyticsApi(apiClient);
  }

  @Bean
  public ChallengeContributionApi challengeContributionApi() {
    return new ChallengeContributionApi(apiClient);
  }

  @Bean
  public ChallengePlatformApi challengePlatformApi() {
    return new ChallengePlatformApi(apiClient);
  }

  @Bean
  public EdamConceptApi edamConceptApi() {
    return new EdamConceptApi(apiClient);
  }

  @Bean
  public OrganizationApi organizationApi() {
    return new OrganizationApi(apiClient);
  }
}
