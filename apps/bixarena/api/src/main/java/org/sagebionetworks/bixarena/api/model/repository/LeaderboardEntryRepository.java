package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntryEntity, UUID> {
  @Query(
    "SELECT e FROM LeaderboardEntryEntity e JOIN FETCH e.model WHERE e.leaderboard = :leaderboard AND e.snapshot = :snapshot"
  )
  Page<LeaderboardEntryEntity> findByLeaderboardAndSnapshot(
    @Param("leaderboard") LeaderboardEntity leaderboard,
    @Param("snapshot") LeaderboardSnapshotEntity snapshot,
    Pageable pageable
  );

  @Query(
    "SELECT e FROM LeaderboardEntryEntity e JOIN FETCH e.model WHERE e.leaderboard = :leaderboard AND e.snapshot = :snapshot " +
    "AND LOWER(e.model.name) LIKE LOWER(CONCAT('%', :search, '%'))"
  )
  Page<LeaderboardEntryEntity> findByLeaderboardAndSnapshotAndModelNameContaining(
    @Param("leaderboard") LeaderboardEntity leaderboard,
    @Param("snapshot") LeaderboardSnapshotEntity snapshot,
    @Param("search") String search,
    Pageable pageable
  );

  Page<LeaderboardEntryEntity> findByLeaderboardAndModel(
    LeaderboardEntity leaderboard,
    ModelEntity model,
    Pageable pageable
  );
}
