package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying user statistics from the auth schema.
 * Note: This repository is read-only and used only for statistics.
 */
@Repository
public interface UserStatsRepository extends JpaRepository<UserEntity, UUID> {
  // count() method is inherited from JpaRepository and will count all users
}
