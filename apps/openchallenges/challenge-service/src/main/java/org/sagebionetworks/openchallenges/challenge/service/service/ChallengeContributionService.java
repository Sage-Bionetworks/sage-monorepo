package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeContributionNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionRoleDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeContributionMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeContributionService {

  private final ChallengeContributionRepository challengeContributionRepository;
  private final ChallengeRepository challengeRepository;

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
      // TODO: Handle challenge participation deletion through API Gateway JWT token exchange
      log.info(
        "Deleting {} challenge contributions for challengeId: {}",
        contributions.size(),
        challengeId
      );
      // For now, just delete the contributions from database
      // Later: implement JWT token exchange for organization service calls
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

    // TODO: Add organization validation through API Gateway JWT token exchange
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
