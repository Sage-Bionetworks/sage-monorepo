package org.sagebionetworks.challenge.model.repository;

import org.sagebionetworks.challenge.model.entity.ChallengePlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengePlatformRepository extends JpaRepository<ChallengePlatformEntity, Long> {

  // Optional<ChallengePlatformEntity> findById(Long id);
}
