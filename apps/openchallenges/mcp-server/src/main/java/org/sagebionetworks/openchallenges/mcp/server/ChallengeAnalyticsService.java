package org.sagebionetworks.openchallenges.mcp.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChallengeAnalyticsService {

  private final RestClient restClient;

  public ChallengeAnalyticsService() {
    this.restClient = RestClient.builder()
      .baseUrl("http://openchallenges-api-gateway:8082/api/v1")
      .build();
  }

  @Tool(
    name = "get_challenges_per_year",
    description = "Get the number of challenges tracked per year"
  )
  public ChallengesPerYear getChallengesPerYear() {
    return restClient
      .get()
      .uri("/challengeAnalytics/challengesPerYear")
      .retrieve()
      .body(ChallengesPerYear.class);
  }
}
