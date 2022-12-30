package org.sagebionetworks.challenge.model.repository;

import java.util.Optional;
import org.sagebionetworks.challenge.model.entity.ChallengePlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengePlatformRepository extends JpaRepository<ChallengePlatformEntity, Long> {

  Optional<ChallengePlatformEntity> findByName(String name);
}
