package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeSubmissionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeSubmissionTypeRepository
  extends JpaRepository<ChallengeSubmissionTypeEntity, Long> {
  void deleteByChallengeId(Long challengeId);
}
