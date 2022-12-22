package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.ChallengePlatformDto;
import org.sagebionetworks.challenge.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.challenge.service.ChallengePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ChallengePlatformApiDelegateImpl implements ChallengePlatformApiDelegate {

  @Autowired ChallengePlatformService challengePlatformService;

  @Override
  public ResponseEntity<ChallengePlatformDto> getChallengePlatform(Long challengePlatformId) {
    return ResponseEntity.ok(challengePlatformService.getChallengePlatform(challengePlatformId));
  }

  @Override
  public ResponseEntity<ChallengePlatformsPageDto> listChallengePlatforms(
      Integer pageNumber, Integer pageSize) {
    return ResponseEntity.ok(challengePlatformService.listChallengePlatforms(pageNumber, pageSize));
  }
}
