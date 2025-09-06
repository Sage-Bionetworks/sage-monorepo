package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
  public ResponseEntity<Void> deleteChallengeContributions(Long challengeId) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logger.info(
      "User {} is deleting all contributions for challenge: {}",
      authentication.getName(),
      challengeId
    );

    challengeContributionService.deleteChallengeContributions(challengeId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
    Long challengeId
  ) {
    return ResponseEntity.ok(challengeContributionService.listChallengeContributions(challengeId));
  }

  @Override
  @PreAuthorize("authentication.principal.admin")
  public ResponseEntity<ChallengeContributionDto> createChallengeContribution(
    Long challengeId,
    ChallengeContributionCreateRequestDto challengeContributionCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logger.info(
      "User {} is adding contribution for challenge: {} with organization: {} and role: {}",
      authentication.getName(),
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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logger.info(
      "User {} is deleting challenge contribution for challengeId: {}, organizationId: {}, role: {}",
      authentication.getName(),
      challengeId,
      organizationId,
      role
    );

    challengeContributionService.deleteChallengeContribution(challengeId, organizationId, role);
    return ResponseEntity.noContent().build();
  }
}
