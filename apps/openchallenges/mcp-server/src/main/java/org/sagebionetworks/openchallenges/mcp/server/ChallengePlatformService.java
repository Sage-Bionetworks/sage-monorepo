package org.sagebionetworks.openchallenges.mcp.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChallengePlatformService {

  private final RestClient restClient = RestClient.builder()
    .baseUrl("http://openchallenges-api-gateway:8082/api/v1")
    .build();

  @Tool(name = "list_challenge_platforms", description = "List available challenge platforms")
  public ChallengePlatformsPage listChallengePlatforms() {
    return restClient
      .get()
      .uri("/challengePlatforms")
      .retrieve()
      .body(ChallengePlatformsPage.class);
  }
}
