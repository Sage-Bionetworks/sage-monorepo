package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.security.ApiKeyAuthenticationFilter;
import org.sagebionetworks.openchallenges.challenge.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ChallengePlatformApiDelegateImpl implements ChallengePlatformApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(
    ChallengePlatformApiDelegateImpl.class
  );

  private final ChallengePlatformService challengePlatformService;

  public ChallengePlatformApiDelegateImpl(ChallengePlatformService challengePlatformService) {
    this.challengePlatformService = challengePlatformService;
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<Void> deleteChallengePlatform(Long challengePlatformId) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting challenge platform: {}",
      user.getUsername(),
      user.getRole(),
      challengePlatformId
    );

    challengePlatformService.deleteChallengePlatform(challengePlatformId);
    return ResponseEntity.noContent().build();
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
