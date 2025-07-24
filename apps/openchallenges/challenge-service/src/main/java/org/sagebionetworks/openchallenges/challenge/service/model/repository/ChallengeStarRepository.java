package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeStar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeStarRepository extends JpaRepository<ChallengeStar, Long> {
  void deleteByChallengeId(Long challengeId);
}
