package org.sagebionetworks.openchallenges.organization.service.service;

import org.sagebionetworks.openchallenges.organization.service.exception.ChallengeParticipationAlreadyExistsException;
import org.sagebionetworks.openchallenges.organization.service.exception.ChallengeParticipationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.mapper.ChallengeParticipationMapper;
import org.sagebionetworks.openchallenges.organization.service.model.repository.ChallengeParticipationRepository;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
import org.sagebionetworks.openchallenges.organization.service.service.OrganizationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeParticipationService {

  private final OrganizationRepository organizationRepository;
  private final ChallengeParticipationRepository challengeParticipationRepository;
  private final OrganizationService organizationService;

  private ChallengeParticipationMapper challengeParticipationMapper =
    new ChallengeParticipationMapper();

  @Transactional(readOnly = false)
  public ChallengeParticipationDto createChallengeParticipation(
    String org,
    ChallengeParticipationCreateRequestDto request
  ) {
    // Find the organization
    OrganizationEntity orgEntity = organizationService.getOrganizationByIdentifier(org);

    // Create the participation entity
    ChallengeParticipationEntity participation = ChallengeParticipationEntity.builder()
      .organization(orgEntity)
      .challengeId(request.getChallengeId())
      .role(request.getRole().getValue())
      .build();

    try {
      ChallengeParticipationEntity savedParticipation = challengeParticipationRepository.save(
        participation
      );
      log.debug(
        "Created challenge participation for org: {}, challengeId: {}, role: {}",
        org,
        request.getChallengeId(),
        request.getRole()
      );
      // TODO: organizationId and role are null
      return challengeParticipationMapper.convertToDto(savedParticipation);
    } catch (DataIntegrityViolationException e) {
      // Check if this is the unique constraint violation
      String message = e.getMessage();
      if (message != null) {
        if (message.contains("uq_participation")) {
          throw new ChallengeParticipationAlreadyExistsException(
            String.format(
              "A participation already exists for org: '%s', challengeId: '%d', role: '%s'",
              org,
              request.getChallengeId(),
              request.getRole()
            )
          );
        }
      }
      // Re-throw the original exception if it's not the constraint we're looking for
      throw e;
    }
  }

  @Transactional(readOnly = false)
  public void deleteChallengeParticipation(
    String org,
    Long challengeId,
    ChallengeParticipationRoleDto role
  ) {
    // Find the organization
    OrganizationEntity orgEntity = organizationService.getOrganizationByIdentifier(org);

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
    log.debug(
      "Successfully deleted challenge participation for org: {}, challengeId: {}, role: {}",
      org,
      challengeId,
      role
    );
  }
}
