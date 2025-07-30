package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.List;
import java.util.Optional;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeContributionRepository
  extends JpaRepository<ChallengeContributionEntity, Long> {
  List<ChallengeContributionEntity> findAllByChallengeId(Long challengeId);

  Optional<ChallengeContributionEntity> findByChallengeIdAndOrganizationIdAndRole(
    Long challengeId,
    Long organizationId,
    String role
  );

  void deleteByChallengeId(Long challengeId);
}
