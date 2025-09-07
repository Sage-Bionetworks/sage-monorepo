package org.sagebionetworks.openchallenges.challenge.service.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeAnalyticsApiDelegateImpl implements ChallengeAnalyticsApiDelegate {

  private final ChallengeAnalyticsService challengeAnalyticsService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:challenges-analytics')")
  public ResponseEntity<ChallengesPerYearDto> getChallengesPerYear() {
    return ResponseEntity.ok(challengeAnalyticsService.getChallengesPerYear());
  }
}
