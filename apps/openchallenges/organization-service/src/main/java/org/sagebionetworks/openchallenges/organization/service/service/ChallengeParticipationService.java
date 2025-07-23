package org.sagebionetworks.openchallenges.organization.service.service;

import java.util.Optional;
import org.sagebionetworks.openchallenges.organization.service.exception.ChallengeParticipationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.repository.ChallengeParticipationRepository;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeParticipationService {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeParticipationService.class);

  private final OrganizationRepository organizationRepository;
  private final ChallengeParticipationRepository challengeParticipationRepository;

  public ChallengeParticipationService(
    OrganizationRepository organizationRepository,
    ChallengeParticipationRepository challengeParticipationRepository
  ) {
    this.organizationRepository = organizationRepository;
    this.challengeParticipationRepository = challengeParticipationRepository;
  }

  public ChallengeParticipationDto createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto requestDto
  ) {
    // TODO: Implement actual creation logic
    return new ChallengeParticipationDto();
  }

  @Transactional(readOnly = false)
  public void deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    // Find the organization by login or id
    String orgLogin = String.valueOf(org);
    Long orgId = null;
    try {
      orgId = Long.valueOf(orgLogin);
    } catch (NumberFormatException ignore) {
      // Ignore - identifier is not a numeric ID
    }
    OrganizationEntity orgEntity = organizationRepository
      .findByIdOrLogin(orgId, orgLogin)
      .orElseThrow(() ->
        new RuntimeException(
          String.format("The organization with the ID or login %s does not exist.", org)
        )
      );

    // Find the participation
    ChallengeParticipationEntity participation = challengeParticipationRepository
      .findByOrganization(orgEntity)
      .stream()
      .filter(p -> p.getChallengeId().equals(challengeId) && p.getRole().equals(role.getValue()))
      .findFirst()
      .orElseThrow(() ->
        new ChallengeParticipationNotFoundException(
          String.format(
            "Challenge participation not found for org: %s, challengeId: %d, role: %s",
            org,
            challengeId,
            role
          )
        )
      );
    challengeParticipationRepository.delete(participation);
    logger.info(
      "Successfully deleted challenge participation for org: {}, challengeId: {}, role: {}",
      org,
      challengeId,
      role
    );
  }
}
