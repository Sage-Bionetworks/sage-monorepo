package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeInputDataTypeRepository
  extends JpaRepository<ChallengeInputDataTypeEntity, Long> {
  @Modifying
  @Query("DELETE FROM ChallengeInputDataTypeEntity c WHERE c.challenge.id = :challengeId")
  void deleteAllByChallengeId(@Param("challengeId") Long challengeId);
}
