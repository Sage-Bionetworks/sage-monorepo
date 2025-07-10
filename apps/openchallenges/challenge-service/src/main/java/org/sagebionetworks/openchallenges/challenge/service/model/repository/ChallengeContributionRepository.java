package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeContributionRepository
  extends JpaRepository<ChallengeContributionEntity, Long> {
  List<ChallengeContributionEntity> findAllByChallenge_id(Long challengeId);

  void deleteByChallengeId(Long challengeId);
}
