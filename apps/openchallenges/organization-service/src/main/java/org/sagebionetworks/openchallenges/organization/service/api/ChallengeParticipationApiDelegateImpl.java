package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.organization.service.service.ChallengeParticipationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ChallengeParticipationApiDelegateImpl implements ChallengeParticipationApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(
    ChallengeParticipationApiDelegateImpl.class
  );

  private final ChallengeParticipationService challengeParticipationService;

  public ChallengeParticipationApiDelegateImpl(
    ChallengeParticipationService challengeParticipationService
  ) {
    this.challengeParticipationService = challengeParticipationService;
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengeParticipationDto> createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto request
  ) {
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is creating a challenge participation for org: {}, challengeId: {}, role: {}",
      user.getUsername(),
      user.getRole(),
      org,
      request.getChallengeId(),
      request.getRole()
    );

    ChallengeParticipationDto createdParticipation =
      challengeParticipationService.createChallengeParticipation(org, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdParticipation);
  }

  @Override
  @PreAuthorize("authentication.principal.admin or authentication.principal.role == 'service'")
  public ResponseEntity<Void> deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting challenge participation for org: {}, challengeId: {}, role: {}",
      user.getUsername(),
      user.getRole(),
      org,
      challengeId,
      role
    );

    challengeParticipationService.deleteChallengeParticipation(org, challengeId, role);
    return ResponseEntity.noContent().build();
  }
}
