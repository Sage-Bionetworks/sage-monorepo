package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.springframework.http.ResponseEntity;

public class ChallengeContributionApiDelegateImpl implements ChallengeContributionApi {

  private final ChallengeContributionService challengeContributionService;

  public ChallengeContributionApiDelegateImpl(
      ChallengeContributionService challengeContributionService) {
    this.challengeContributionService = challengeContributionService;
  }

  @Override
  public ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
      Long challengeId) {
    return ResponseEntity.ok(challengeContributionService.listChallengeContributions(challengeId));
  }
}
