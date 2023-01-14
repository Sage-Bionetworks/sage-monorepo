package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.sagebionetworks.challenge.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengeApiDelegateImpl implements ChallengeApiDelegate {

  @Autowired ChallengeService challengeService;

  @Override
  public ResponseEntity<ChallengesPageDto> listChallenges(ChallengeSearchQueryDto query) {
    return ResponseEntity.ok(challengeService.listChallenges(query));
  }

  // @Override
  // public ResponseEntity<ChallengeDto> getChallenge(String challengeLogin) {
  //   return ResponseEntity.ok(challengeService.getChallenge(challengeLogin));
  // }
}
