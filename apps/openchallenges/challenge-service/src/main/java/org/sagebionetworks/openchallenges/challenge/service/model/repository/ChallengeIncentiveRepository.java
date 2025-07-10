package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeIncentiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeIncentiveRepository
  extends JpaRepository<ChallengeIncentiveEntity, Long> {
  void deleteByChallengeId(Long challengeId);
}
