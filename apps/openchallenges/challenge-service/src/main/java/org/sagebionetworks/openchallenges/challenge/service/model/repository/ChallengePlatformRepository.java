package org.sagebionetworks.openchallenges.challenge.service.model.repository;

import java.util.Optional;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengePlatformRepository
  extends JpaRepository<ChallengePlatformEntity, Long>, CustomChallengePlatformRepository {
  Optional<ChallengePlatformEntity> findById(Long id);
}
