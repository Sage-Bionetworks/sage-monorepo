package org.sagebionetworks.openchallenges.auth.service.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
  List<ApiKey> findByUserOrderByCreatedAtDesc(User user);

  Optional<ApiKey> findByKeyHash(String keyHash);

  @Query(
    "SELECT a FROM ApiKey a WHERE a.keyHash = :keyHash AND (a.expiresAt IS NULL OR a.expiresAt > :now)"
  )
  Optional<ApiKey> findValidApiKey(
    @Param("keyHash") String keyHash,
    @Param("now") OffsetDateTime now
  );

  @Query("SELECT COUNT(a) FROM ApiKey a WHERE a.user = :user")
  long countByUser(@Param("user") User user);

  void deleteByUser(User user);

  @Query("DELETE FROM ApiKey a WHERE a.expiresAt IS NOT NULL AND a.expiresAt < :now")
  void deleteExpiredApiKeys(@Param("now") OffsetDateTime now);
}
