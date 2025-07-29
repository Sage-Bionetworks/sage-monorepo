package org.sagebionetworks.openchallenges.challenge.service.service;

import feign.FeignException;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.client.OrganizationServiceClient;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeContributionNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeParticipationDeleteException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeParticipationNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeContributionMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeContributionService {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeContributionService.class);

  private final ChallengeContributionRepository challengeContributionRepository;
  private final ChallengeRepository challengeRepository;
  private final OrganizationServiceClient organizationServiceClient;

  private final ChallengeContributionMapper challengeContributionMapper =
    new ChallengeContributionMapper();

  public ChallengeContributionService(
    ChallengeContributionRepository challengeContributionRepository,
    ChallengeRepository challengeRepository,
    OrganizationServiceClient organizationServiceClient
  ) {
    this.challengeContributionRepository = challengeContributionRepository;
    this.challengeRepository = challengeRepository;
    this.organizationServiceClient = organizationServiceClient;
  }

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
  public void deleteAllChallengeContributions(Long challengeId) {
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

    // Validate organization exists by querying the organization service
    try {
      organizationServiceClient.getOrganization(request.getOrganizationId());
    } catch (FeignException.NotFound e) {
      throw new OrganizationNotFoundException(
        "Organization not found with id: " + request.getOrganizationId()
      );
    } catch (FeignException e) {
      throw new RuntimeException(
        "Failed to validate organization with id: " +
        request.getOrganizationId() +
        ". Reason: " +
        e.getMessage(),
        e
      );
    }

    // Create the contribution entity
    ChallengeContributionEntity entity = ChallengeContributionEntity.builder()
      .challenge(challenge)
      .organizationId(request.getOrganizationId())
      .role(request.getRole().getValue())
      .build();

    try {
      // Save the entity
      ChallengeContributionEntity savedEntity = challengeContributionRepository.save(entity);

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

    // Delete this organization's challenge participation
    try {
      organizationServiceClient.deleteChallengeParticipation(
        String.valueOf(existingContribution.getOrganizationId()),
        challengeId,
        existingContribution.getRole()
      );
    } catch (FeignException.NotFound e) {
      throw new ChallengeParticipationNotFoundException(
        "Challenge participation for organization " +
        existingContribution.getOrganizationId() +
        " in challenge " +
        challengeId +
        " with role " +
        existingContribution.getRole() +
        " not found. Cannot delete."
      );
    } catch (FeignException e) {
      if (e.status() == 401) {
        throw new ChallengeParticipationDeleteException(
          "Failed to delete challenge participation for organization " +
          existingContribution.getOrganizationId() +
          " due to authentication error (401 Unauthorized). " +
          "Reason: " +
          e.getMessage()
        );
      }
      throw new RuntimeException(
        "Failed to delete challenge participation for organization " +
        existingContribution.getOrganizationId() +
        ". Reason: " +
        e.getMessage(),
        e
      );
    }

    // Delete the contribution
    challengeContributionRepository.delete(existingContribution);
    logger.debug(
      "Successfully deleted challenge contribution for challengeId: {}, organizationId: {}, role: {}",
      organizationId,
      challengeId,
      role
    );
  }
}
