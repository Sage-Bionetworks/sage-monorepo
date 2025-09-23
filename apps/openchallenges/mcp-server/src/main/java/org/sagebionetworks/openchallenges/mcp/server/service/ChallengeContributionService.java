package org.sagebionetworks.openchallenges.mcp.server.service;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeContributionApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeContributionService {

  private final ChallengeContributionApi challengeContributionApi;

  @Tool(
    name = "list_challenge_contributions",
    description = """
    Lists contributions made by organizations to challenges.

    Guidelines for using this tool:
    - You can specify a challenge ID (`challengeId`) to find all organizations that contributed to that challenge.
    """
  )
  public ChallengeContributionsPage listChallengeContributions(
    @ToolParam(description = "The ID of the challenge to list contributions for.") Long challengeId
  ) {
    return challengeContributionApi.listChallengeContributions(challengeId);
  }
}
