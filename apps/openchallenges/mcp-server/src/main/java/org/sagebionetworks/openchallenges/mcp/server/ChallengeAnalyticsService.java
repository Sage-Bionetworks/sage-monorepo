package org.sagebionetworks.openchallenges.mcp.server;

import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ChallengeAnalyticsService {

  private final ChallengeAnalyticsApi challengeAnalyticsApi;

  public ChallengeAnalyticsService(ChallengeAnalyticsApi challengeAnalyticsApi) {
    this.challengeAnalyticsApi = challengeAnalyticsApi;
  }

  @Tool(
    name = "get_challenges_per_year",
    description = "Get the number of challenges tracked per year"
  )
  public ChallengesPerYear getChallengesPerYear() {
    return challengeAnalyticsApi.getChallengesPerYear();
  }
}
