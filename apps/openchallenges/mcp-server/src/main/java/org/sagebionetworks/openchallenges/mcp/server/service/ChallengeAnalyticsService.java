package org.sagebionetworks.openchallenges.mcp.server.service;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeAnalyticsService {

  private final ChallengeAnalyticsApi challengeAnalyticsApi;

  @Tool(
    name = "get_challenges_per_year",
    description = "Get the number of challenges tracked per year"
  )
  public ChallengesPerYear getChallengesPerYear() {
    return challengeAnalyticsApi.getChallengesPerYear();
  }
}
