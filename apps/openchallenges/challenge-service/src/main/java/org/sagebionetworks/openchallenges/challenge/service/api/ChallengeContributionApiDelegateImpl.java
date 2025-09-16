package org.sagebionetworks.openchallenges.challenge.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.service.ChallengeContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeContributionApiDelegateImpl implements ChallengeContributionApiDelegate {

  private final ChallengeContributionService challengeContributionService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_update:challenges')")
  public ResponseEntity<Void> deleteChallengeContributions(Long challengeId) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
      "User {} is deleting all contributions for challenge: {}",
      authentication.getName(),
      challengeId
    );

    challengeContributionService.deleteChallengeContributions(challengeId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:challenges')")
  public ResponseEntity<ChallengeContributionsPageDto> listChallengeContributions(
    Long challengeId
  ) {
    return ResponseEntity.ok(challengeContributionService.listChallengeContributions(challengeId));
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_update:challenges')")
  public ResponseEntity<ChallengeContributionDto> createChallengeContribution(
    Long challengeId,
    ChallengeContributionCreateRequestDto challengeContributionCreateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
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
  @PreAuthorize("hasAuthority('SCOPE_update:challenges')")
  public ResponseEntity<Void> deleteChallengeContribution(
    Long challengeId,
    Long organizationId,
    ChallengeContributionRoleDto role
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
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
