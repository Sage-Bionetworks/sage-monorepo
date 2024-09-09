package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengeContributionApiDelegateImpl implements ChallengeContributionApiDelegate {

  private final ChallengeContributionService challengeContributionService;

  public ChallengeContributionApiDelegateImpl(
    ChallengeContributionService challengeContributionService
  ) {
    this.challengeContributionService = challengeContributionService;
  }

  @Override
  public ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
    Long challengeId
  ) {
    return ResponseEntity.ok(challengeContributionService.listChallengeContributions(challengeId));
  }
}
