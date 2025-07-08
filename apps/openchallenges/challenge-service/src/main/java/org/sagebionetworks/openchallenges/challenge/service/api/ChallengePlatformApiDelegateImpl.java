package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengePlatformService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengePlatformApiDelegateImpl implements ChallengePlatformApiDelegate {

  private final ChallengePlatformService challengePlatformService;

  public ChallengePlatformApiDelegateImpl(ChallengePlatformService challengePlatformService) {
    this.challengePlatformService = challengePlatformService;
  }

  @Override
  public ResponseEntity<ChallengePlatformDto> getChallengePlatform(Long challengePlatformId) {
    return ResponseEntity.ok(challengePlatformService.getChallengePlatform(challengePlatformId));
  }

  @Override
  public ResponseEntity<ChallengePlatformsPageDto> listChallengePlatforms(
    ChallengePlatformSearchQueryDto query
  ) {
    return ResponseEntity.ok(challengePlatformService.listChallengePlatforms(query));
  }
}
