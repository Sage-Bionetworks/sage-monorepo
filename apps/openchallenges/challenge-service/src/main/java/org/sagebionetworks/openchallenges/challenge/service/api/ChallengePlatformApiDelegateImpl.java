package org.sagebionetworks.openchallenges.challenge.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformUpdateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengePlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengePlatformApiDelegateImpl implements ChallengePlatformApiDelegate {

  private final ChallengePlatformService challengePlatformService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_delete:challenge-platforms')")
  public ResponseEntity<Void> deleteChallengePlatform(Long challengePlatformId) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
      "User {} is deleting challenge platform: {}",
      authentication.getName(),
      challengePlatformId
    );

    challengePlatformService.deleteChallengePlatform(challengePlatformId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:challenge-platforms')")
  public ResponseEntity<ChallengePlatformDto> getChallengePlatform(Long challengePlatformId) {
    return ResponseEntity.ok(challengePlatformService.getChallengePlatform(challengePlatformId));
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:challenge-platforms')")
  public ResponseEntity<ChallengePlatformsPageDto> listChallengePlatforms(
    ChallengePlatformSearchQueryDto query
  ) {
    return ResponseEntity.ok(challengePlatformService.listChallengePlatforms(query));
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_create:challenge-platforms')")
  public ResponseEntity<ChallengePlatformDto> createChallengePlatform(
    ChallengePlatformCreateRequestDto challengePlatformCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("User {} is creating a new challenge platform", authentication.getName());

    ChallengePlatformDto createdPlatform = challengePlatformService.createChallengePlatform(
      challengePlatformCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPlatform);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_update:challenge-platforms')")
  public ResponseEntity<ChallengePlatformDto> updateChallengePlatform(
    Long challengePlatformId,
    ChallengePlatformUpdateRequestDto challengePlatformUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
      "User {} is updating challenge platform {}",
      authentication.getName(),
      challengePlatformId
    );

    ChallengePlatformDto updatedPlatform = challengePlatformService.updateChallengePlatform(
      challengePlatformId,
      challengePlatformUpdateRequestDto
    );

    return ResponseEntity.ok(updatedPlatform);
  }
}
