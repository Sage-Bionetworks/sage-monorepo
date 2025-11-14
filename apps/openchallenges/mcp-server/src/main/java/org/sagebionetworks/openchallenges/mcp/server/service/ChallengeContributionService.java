package org.sagebionetworks.openchallenges.mcp.server.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeContributionApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ChallengeContributionService {

  private final ChallengeContributionApi challengeContributionApi;

  @Tool(
    name = "list_challenge_contributions",
    description = """
    Retrieve organization contributions for a specific challenge.

    Usage:
    - Always supply the target challengeId.
    - Use this AFTER discovering a challenge via list_challenges to enumerate contributing organizations (for attribution, sponsorship, etc.).

    Example:
    - "Who contributed to challenge 42?" -> challengeId=42.
    """
  )
  public ChallengeContributionsPage listChallengeContributions(
    @ToolParam(description = "Challenge ID (long > 0). Required.") @Positive Long challengeId
  ) {
    return challengeContributionApi.listChallengeContributions(challengeId);
  }
}
