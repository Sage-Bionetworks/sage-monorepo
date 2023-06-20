package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeContributionsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChallengeContributionService {

  private static final Logger LOG = LoggerFactory.getLogger(ChallengeContributionService.class);

  private final ChallengeContributionRepository challengeContributionRepository;

  public ChallengeContributionService(
      ChallengeContributionRepository challengeContributionRepository) {
    this.challengeContributionRepository = challengeContributionRepository;
  }

  public ChallengeContributionsPageDto listChallengeContributions(Long challengeId) {

    LOG.info("listChallengeContributions {}", challengeId);

    List<ChallengeContributionEntity> entities =
        challengeContributionRepository.findAllByChallengeId(challengeId);

    LOG.info("entities {}", entities);
    return ChallengeContributionsPageDto.builder().build();
  }
}
