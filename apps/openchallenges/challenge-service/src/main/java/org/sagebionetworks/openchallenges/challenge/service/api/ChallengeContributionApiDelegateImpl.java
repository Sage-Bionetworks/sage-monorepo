package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionUpdateRequestDto;
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
    Long challengeContributionId
  ) {
    ChallengeContributionDto contribution = challengeContributionService.getChallengeContribution(
      challengeId,
      challengeContributionId
    );
    return ResponseEntity.ok(contribution);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengeContributionDto> addChallengeContribution(
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

    ChallengeContributionDto response =
      challengeContributionService.addChallengeContribution(
        challengeId,
        challengeContributionCreateRequestDto
      );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengeContributionDto> updateChallengeContribution(
    Long challengeId,
    Long challengeContributionId,
    ChallengeContributionUpdateRequestDto challengeContributionUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is updating contribution {} for challenge: {} with organization: {} and role: {}",
      user.getUsername(),
      user.getRole(),
      challengeContributionId,
      challengeId,
      challengeContributionUpdateRequestDto.getOrganizationId(),
      challengeContributionUpdateRequestDto.getRole()
    );

    ChallengeContributionDto updatedContribution =
      challengeContributionService.updateChallengeContribution(
        challengeId,
        challengeContributionId,
        challengeContributionUpdateRequestDto
      );

    return ResponseEntity.ok(updatedContribution);
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<Void> deleteChallengeContribution(
    Long challengeId,
    Long challengeContributionId
  ) {
    // Log the authenticated user for audit purposes
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    logger.info(
      "User {} (role: {}) is deleting contribution {} for challenge: {}",
      user.getUsername(),
      user.getRole(),
      challengeContributionId,
      challengeId
    );

    challengeContributionService.deleteChallengeContribution(challengeId, challengeContributionId);
    return ResponseEntity.noContent().build();
  }
}
