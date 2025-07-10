package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeContributionMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeContributionService {

  private final ChallengeContributionRepository challengeContributionRepository;

  private final ChallengeContributionMapper challengeContributionMapper =
    new ChallengeContributionMapper();

  public ChallengeContributionService(
    ChallengeContributionRepository challengeContributionRepository
  ) {
    this.challengeContributionRepository = challengeContributionRepository;
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
}
