package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengeAnalyticsApiDelegateImpl implements ChallengeAnalyticsApiDelegate {

  private final ChallengeAnalyticsService challengeAnalyticsService;

  public ChallengeAnalyticsApiDelegateImpl(ChallengeAnalyticsService challengeAnalyticsService) {
    this.challengeAnalyticsService = challengeAnalyticsService;
  }

  @Override
  public ResponseEntity<ChallengesPerYearDto> getChallengesPerYear() {
    return ResponseEntity.ok(challengeAnalyticsService.getChallengesPerYear());
  }
}
