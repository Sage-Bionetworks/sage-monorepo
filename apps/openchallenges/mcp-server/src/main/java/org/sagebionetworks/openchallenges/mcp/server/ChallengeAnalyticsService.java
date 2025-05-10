package org.sagebionetworks.openchallenges.mcp.server;

import org.sagebionetworks.openchallenges.api.client.ApiClient;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ChallengeAnalyticsService {

  private final ChallengeAnalyticsApi challengeAnalyticsApi;

  public ChallengeAnalyticsService() {
    ApiClient defaultClient = new ApiClient();
    defaultClient.setBasePath("http://openchallenges-api-gateway:8082/api/v1");
    challengeAnalyticsApi = new ChallengeAnalyticsApi(defaultClient);
  }

  @Tool(
    name = "get_challenges_per_year",
    description = "Get the number of challenges tracked per year"
  )
  public ChallengesPerYear getChallengesPerYear() {
    return challengeAnalyticsApi.getChallengesPerYear();
  }
}
