package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionCreateResponseDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeContributionMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeContributionService {

  private final ChallengeContributionRepository challengeContributionRepository;
  private final ChallengeRepository challengeRepository;

  private final ChallengeContributionMapper challengeContributionMapper =
    new ChallengeContributionMapper();

  public ChallengeContributionService(
    ChallengeContributionRepository challengeContributionRepository,
    ChallengeRepository challengeRepository
  ) {
    this.challengeContributionRepository = challengeContributionRepository;
    this.challengeRepository = challengeRepository;
  }

  public ChallengeContributionsPageDto listChallengeContributions(Long challengeId) {
    List<ChallengeContributionEntity> entities =
      challengeContributionRepository.findAllByChallenge_id(challengeId);
    List<ChallengeContributionDto> contributions = challengeContributionMapper.convertToDtoList(
      entities
    );

    return ChallengeContributionsPageDto.builder()
      .challengeContributions(contributions)
      .number(0)
      .size(contributions.size())
      .totalElements(Long.valueOf(contributions.size()))
      .totalPages(1)
      .hasNext(false)
      .hasPrevious(false)
      .build();
  }

  @Transactional
  public void deleteAllChallengeContributions(Long challengeId) {
    challengeContributionRepository.deleteByChallengeId(challengeId);
  }

  @Transactional
  public ChallengeContributionCreateResponseDto addChallengeContribution(
    Long challengeId,
    ChallengeContributionCreateRequestDto request
  ) {
    // Verify the challenge exists
    ChallengeEntity challenge = challengeRepository
      .findById(challengeId)
      .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));

    // Create the contribution entity
    ChallengeContributionEntity entity = ChallengeContributionEntity.builder()
      .challenge(challenge)
      .organizationId(request.getOrganizationId())
      .role(request.getRole().getValue())
      .build();

    // Save the entity
    ChallengeContributionEntity savedEntity = challengeContributionRepository.save(entity);

    // Return the response with the generated ID
    return new ChallengeContributionCreateResponseDto(savedEntity.getId());
  }
}
