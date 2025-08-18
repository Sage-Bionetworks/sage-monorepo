package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntity, UUID> {
  Optional<LeaderboardEntity> findBySlug(String slug);
}
