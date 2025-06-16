package org.sagebionetworks.openchallenges.mcp.server;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeContributionApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionRole;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ChallengeContributionService {

  private final ChallengeContributionApi challengeContributionApi;

  public ChallengeContributionService(ChallengeContributionApi challengeContributionApi) {
    this.challengeContributionApi = challengeContributionApi;
  }

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
