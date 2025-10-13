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
    description = """
    Retrieve historical counts of tracked challenges per calendar year.

    Usage:
    - Call to display longitudinal growth / trend analysis.
    - Combine with user request for 'trend', 'history', or 'yearly numbers'.

    Example:
    - "Show how challenge numbers evolved over time" -> call directly.
    """
  )
  public ChallengesPerYear getChallengesPerYear() {
    return challengeAnalyticsApi.getChallengesPerYear();
  }
}
