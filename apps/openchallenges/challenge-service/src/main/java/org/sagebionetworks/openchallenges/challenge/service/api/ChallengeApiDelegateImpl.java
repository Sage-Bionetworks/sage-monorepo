package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengeApiDelegateImpl implements ChallengeApiDelegate {

  //@Autowired ChallengeService challengeService;
  private final ChallengeService challengeService;

  public ChallengeApiDelegateImpl(ChallengeService challengeService) {
    this.challengeService = challengeService;
  }

  @Override
  public ResponseEntity<ChallengeDto> getChallenge(Long challengeId) {
    return ResponseEntity.ok(challengeService.getChallenge(challengeId));
  }

  @Override
  public ResponseEntity<ChallengesPageDto> listChallenges(ChallengeSearchQueryDto query) {
    return ResponseEntity.ok(challengeService.listChallenges(query));
  }
}
