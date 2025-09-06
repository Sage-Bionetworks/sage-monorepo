package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.service.ChallengeParticipationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeParticipationApiDelegateImpl implements ChallengeParticipationApiDelegate {

  private final ChallengeParticipationService challengeParticipationService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_write:org')")
  public ResponseEntity<ChallengeParticipationDto> createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto request
  ) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info(
        "User {} is creating a challenge participation for org: {}, challengeId: {}, role: {}",
        authentication.getName(),
        org,
        request.getChallengeId(),
        request.getRole()
      );
    }

    ChallengeParticipationDto createdParticipation =
      challengeParticipationService.createChallengeParticipation(org, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdParticipation);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_delete:org')")
  public ResponseEntity<Void> deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info(
        "User {} is deleting challenge participation for org: {}, challengeId: {}, role: {}",
        authentication.getName(),
        org,
        challengeId,
        role
      );
    }

    challengeParticipationService.deleteChallengeParticipation(org, challengeId, role);
    return ResponseEntity.noContent().build();
  }
}
