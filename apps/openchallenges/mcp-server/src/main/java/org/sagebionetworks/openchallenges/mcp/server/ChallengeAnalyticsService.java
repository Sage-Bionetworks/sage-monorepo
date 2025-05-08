package org.sagebionetworks.openchallenges.mcp.server;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChallengeAnalyticsService {

  private final RestClient restClient;

  public ChallengeAnalyticsService() {
    this.restClient = RestClient.builder().baseUrl("http://localhost:8082/api/v1").build();
  }

  public ChallengesPerYear fetch() {
    return restClient
      .get()
      .uri("/challengeAnalytics/challengesPerYear")
      .retrieve()
      .body(ChallengesPerYear.class);
  }
}
