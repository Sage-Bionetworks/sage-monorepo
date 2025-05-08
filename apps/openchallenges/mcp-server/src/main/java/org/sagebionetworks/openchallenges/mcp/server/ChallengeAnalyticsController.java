package org.sagebionetworks.openchallenges.mcp.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class ChallengeAnalyticsController {

  private final ChallengeAnalyticsService service;

  public ChallengeAnalyticsController(ChallengeAnalyticsService service) {
    this.service = service;
  }

  @GetMapping("/challenges-per-year")
  public ChallengesPerYear getChallengesPerYear() {
    return service.fetch();
  }
}
