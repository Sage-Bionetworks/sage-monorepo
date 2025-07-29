package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.security.AuthenticatedUser;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ChallengeContributionApiDelegateImpl implements ChallengeContributionApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(
    ChallengeContributionApiDelegateImpl.class
  );

  private final ChallengeContributionService challengeContributionService;

  public ChallengeContributionApiDelegateImpl(
    ChallengeContributionService challengeContributionService
  ) {
    this.challengeContributionService = challengeContributionService;
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<Void> deleteAllChallengeContributions(Long challengeId) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting all contributions for challenge: {}",
      user.getUsername(),
      user.getRole(),
      challengeId
    );

    challengeContributionService.deleteAllChallengeContributions(challengeId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
    Long challengeId
  ) {
    return ResponseEntity.ok(challengeContributionService.listChallengeContributions(challengeId));
  }

  @Override
  public ResponseEntity<ChallengeContributionDto> getChallengeContribution(
    Long challengeId,
    Long organizationId,
    ChallengeContributionRoleDto role
  ) {
    ChallengeContributionDto contribution = challengeContributionService.getChallengeContribution(
      challengeId,
      organizationId,
      role
    );
    return ResponseEntity.ok(contribution);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengeContributionDto> createChallengeContribution(
    Long challengeId,
    ChallengeContributionCreateRequestDto challengeContributionCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is adding contribution for challenge: {} with organization: {} and role: {}",
      user.getUsername(),
      user.getRole(),
      challengeId,
      challengeContributionCreateRequestDto.getOrganizationId(),
      challengeContributionCreateRequestDto.getRole()
    );

    ChallengeContributionDto createdContribution =
      challengeContributionService.createChallengeContribution(
        challengeId,
        challengeContributionCreateRequestDto
      );

    return ResponseEntity.status(HttpStatus.CREATED).body(createdContribution);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<Void> deleteChallengeContribution(
    Long challengeId,
    Long organizationId,
    ChallengeContributionRoleDto role
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting challenge contribution for challengeId: {}, organizationId: {}, role: {}",
      user.getUsername(),
      user.getRole(),
      challengeId,
      organizationId,
      role
    );

    challengeContributionService.deleteChallengeContribution(challengeId, organizationId, role);
    return ResponseEntity.noContent().build();
  }
}
