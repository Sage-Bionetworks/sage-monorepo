package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformUpdateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengePlatformDto> createChallengePlatform(
    ChallengePlatformCreateRequestDto challengePlatformCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is creating a new challenge platform",
      user.getUsername(),
      user.getRole()
    );

    ChallengePlatformDto createdPlatform = challengePlatformService.createChallengePlatform(
      challengePlatformCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPlatform);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengePlatformDto> updateChallengePlatform(
    Long challengePlatformId,
    ChallengePlatformUpdateRequestDto challengePlatformUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is updating challenge platform {}",
      user.getUsername(),
      user.getRole(),
      challengePlatformId
    );

    ChallengePlatformDto updatedPlatform = challengePlatformService.updateChallengePlatform(
      challengePlatformId,
      challengePlatformUpdateRequestDto
    );

    return ResponseEntity.ok(updatedPlatform);
  }
}
