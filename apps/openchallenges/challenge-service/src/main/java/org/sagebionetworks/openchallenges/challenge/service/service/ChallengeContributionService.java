package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.client.OrganizationServiceClient;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeContributionNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.organization.ChallengeParticipationRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeContributionMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeContributionService {

  private final ChallengeContributionRepository challengeContributionRepository;
  private final ChallengeRepository challengeRepository;
  private final OrganizationServiceClient organizationServiceClient;

  private final ChallengeContributionMapper challengeContributionMapper =
    new ChallengeContributionMapper();

  public ChallengeContributionsPageDto listChallengeContributions(Long challengeId) {
    List<ChallengeContributionEntity> entities =
      challengeContributionRepository.findAllByChallengeId(challengeId);
    List<ChallengeContributionDto> contributions = challengeContributionMapper.convertToDtoList(
      entities
    );

    return ChallengeContributionsPageDto.builder()
      .challengeContributions(contributions)
      .number(0)
      .size(contributions.size())
      .totalElements((long) contributions.size())
      .totalPages(1)
      .hasNext(false)
      .hasPrevious(false)
      .build();
  }

  public ChallengeContributionDto getChallengeContribution(
    Long challengeId,
    Long organizationId,
    ChallengeContributionRoleDto role
  ) {
    // Verify the challenge exists
    challengeRepository
      .findById(challengeId)
      .orElseThrow(() ->
        new ChallengeNotFoundException("Challenge not found with id: " + challengeId)
      );

    // Find the contribution
    ChallengeContributionEntity contribution = challengeContributionRepository
      .findByChallengeIdAndOrganizationIdAndRole(challengeId, organizationId, role.getValue())
      .orElseThrow(() ->
        new ChallengeContributionNotFoundException(
          "Challenge contribution not found for challenge " +
          challengeId +
          ", organization " +
          organizationId +
          ", and role " +
          role.getValue()
        )
      );

    // Return the contribution as DTO
    return challengeContributionMapper.convertToDto(contribution);
  }

  @Transactional
  public void deleteChallengeContributions(Long challengeId) {
    // Fetch all existing contributions for this challenge
    List<ChallengeContributionEntity> contributions =
      challengeContributionRepository.findAllByChallengeId(challengeId);

    if (!contributions.isEmpty()) {
      log.info(
        "Deleting {} challenge contributions for challengeId: {}",
        contributions.size(),
        challengeId
      );

      // Delete challenge participations for each organization/role combination
      for (ChallengeContributionEntity contribution : contributions) {
        try {
          organizationServiceClient.deleteChallengeParticipation(
            contribution.getOrganizationId().toString(),
            challengeId,
            contribution.getRole()
          );
          log.debug(
            "Successfully deleted challenge participation for org: {}, challenge: {}, role: {}",
            contribution.getOrganizationId(),
            challengeId,
            contribution.getRole()
          );
        } catch (RestClientException e) {
          if (e.getMessage() != null && e.getMessage().contains("404")) {
            // Participation doesn't exist, which is fine - continue with other deletions
            log.debug(
              "No challenge participation found for org: {}, challenge: {}, role: {} - continuing",
              contribution.getOrganizationId(),
              challengeId,
              contribution.getRole()
            );
          } else {
            log.error(
              "Failed to delete challenge participation for org: {}, challenge: {}, role: {}. Error: {}",
              contribution.getOrganizationId(),
              challengeId,
              contribution.getRole(),
              e.getMessage()
            );
            // Continue with other deletions even if one fails
          }
        }
      }
    }

    // Delete the contributions from the database
    challengeContributionRepository.deleteByChallengeId(challengeId);
  }

  @Transactional
  public ChallengeContributionDto createChallengeContribution(
    Long challengeId,
    ChallengeContributionCreateRequestDto request
  ) {
    // Verify the challenge exists
    ChallengeEntity challenge = challengeRepository
      .findById(challengeId)
      .orElseThrow(() ->
        new ChallengeNotFoundException("Challenge not found with id: " + challengeId)
      );

    // Validate organization exists by querying the organization service using JWT token exchange
    // TODO: Fix token exchange implementation - temporarily disabled for testing
    /*
    try {
      // OrganizationServiceClient.getOrganization() already handles token exchange internally
      organizationServiceClient.getOrganization(request.getOrganizationId());
    } catch (RestClientException e) {
      if (e.getMessage() != null && e.getMessage().contains("404")) {
        throw new OrganizationNotFoundException(
          "Organization not found with id: " + request.getOrganizationId()
        );
      }
      throw new RuntimeException(
        "Failed to validate organization with id: " +
        request.getOrganizationId() +
        ". Reason: " +
        e.getMessage(),
        e
      );
    }
    */

    log.info(
      "Creating contribution for organization: {} on challenge: {}",
      request.getOrganizationId(),
      challengeId
    );

    // Create the contribution entity
    ChallengeContributionEntity entity = ChallengeContributionEntity.builder()
      .challenge(challenge)
      .organizationId(request.getOrganizationId())
      .role(request.getRole().getValue())
      .build();

    try {
      // Save the entity
      ChallengeContributionEntity savedEntity = challengeContributionRepository.save(entity);

      // Create a challenge participation for this organization-challenge relationship
      // TODO: Fix token exchange implementation - temporarily disabled for testing
      /*
      try {
        // Convert the contribution role to participation role
        ChallengeParticipationRoleDto participationRole = 
          ChallengeParticipationRoleDto.fromValue(request.getRole().getValue());
        
        ChallengeParticipationCreateRequestDto participationRequest = 
          new ChallengeParticipationCreateRequestDto(challengeId, participationRole);
        
        organizationServiceClient.createChallengeParticipation(
          request.getOrganizationId().toString(),
          participationRequest
        );
        log.debug(
          "Successfully created challenge participation for org: {}, challenge: {}, role: {}",
          request.getOrganizationId(),
          challengeId,
          request.getRole().getValue()
        );
      } catch (RestClientException e) {
        if (e.getMessage() != null && e.getMessage().contains("409")) {
          // Participation already exists, which is fine - the contribution was still created
          log.debug(
            "Challenge participation already exists for org: {}, challenge: {}, role: {} - continuing",
            request.getOrganizationId(),
            challengeId,
            request.getRole().getValue()
          );
        } else {
          log.error(
            "Failed to create challenge participation for org: {}, challenge: {}, role: {}. Error: {}",
            request.getOrganizationId(),
            challengeId,
            request.getRole().getValue(),
            e.getMessage()
          );
          // Note: We could decide to rollback the contribution creation here,
          // but for now we'll allow the contribution to exist without participation
        }
      }
      */

      // Return the full contribution DTO
      return challengeContributionMapper.convertToDto(savedEntity);
    } catch (DataIntegrityViolationException e) {
      // Check if this is the unique constraint violation
      if (e.getMessage() != null && e.getMessage().contains("unique_contribution")) {
        throw new DuplicateContributionException(
          String.format(
            "A contribution with role '%s' already exists for organization %d in challenge %d.",
            request.getRole().getValue(),
            request.getOrganizationId(),
            challengeId
          ),
          e
        );
      }
      // Re-throw the original exception if it's not the constraint we're looking for
      throw e;
    }
  }

  @Transactional
  public void deleteChallengeContribution(
    Long challengeId,
    Long organizationId,
    ChallengeContributionRoleDto role
  ) {
    // Verify the challenge exists
    challengeRepository
      .findById(challengeId)
      .orElseThrow(() ->
        new ChallengeNotFoundException("Challenge not found with id: " + challengeId)
      );

    // Find the existing contribution
    ChallengeContributionEntity existingContribution = challengeContributionRepository
      .findByChallengeIdAndOrganizationIdAndRole(challengeId, organizationId, role.getValue())
      .orElseThrow(() ->
        new ChallengeContributionNotFoundException(
          "Challenge contribution not found for challenge " +
          challengeId +
          ", organization " +
          organizationId +
          ", and role " +
          role.getValue()
        )
      );

    // TODO: Handle challenge participation deletion through API Gateway JWT token exchange
    log.info(
      "Deleting contribution for organization: {} on challenge: {} with role: {}",
      existingContribution.getOrganizationId(),
      challengeId,
      existingContribution.getRole()
    );

    // Delete any challenge participation for this organization before removing the contribution
    try {
      organizationServiceClient.deleteChallengeParticipation(
        existingContribution.getOrganizationId().toString(),
        challengeId,
        existingContribution.getRole()
      );
      log.debug(
        "Successfully deleted challenge participation for organizationId: {}, challengeId: {}",
        existingContribution.getOrganizationId(),
        challengeId
      );
    } catch (RestClientException e) {
      if (e.getMessage() != null && e.getMessage().contains("404")) {
        // Participation doesn't exist, which is fine - continue with contribution deletion
        log.debug(
          "No challenge participation found for organizationId: {}, challengeId: {} - continuing with contribution deletion",
          existingContribution.getOrganizationId(),
          challengeId
        );
      } else {
        log.error(
          "Failed to delete challenge participation for organizationId: {}, challengeId: {}. Error: {}",
          existingContribution.getOrganizationId(),
          challengeId,
          e.getMessage()
        );
        throw new RuntimeException(
          "Failed to delete challenge participation for organization " +
          existingContribution.getOrganizationId() +
          " in challenge " +
          challengeId +
          ". Reason: " +
          e.getMessage(),
          e
        );
      }
    }

    // Delete the contribution from database
    challengeContributionRepository.delete(existingContribution);
    log.debug(
      "Successfully deleted challenge contribution for challengeId: {}, organizationId: {}, role: {}",
      challengeId,
      organizationId,
      role.getValue()
    );
  }
}
