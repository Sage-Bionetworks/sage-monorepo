package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.projection.SnapshotWithEntryCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardSnapshotRepository
  extends JpaRepository<LeaderboardSnapshotEntity, UUID> {
  Page<LeaderboardSnapshotEntity> findByLeaderboardOrderByCreatedAtDesc(
    LeaderboardEntity leaderboard,
    Pageable pageable
  );

  @Query(
    "SELECT s FROM LeaderboardSnapshotEntity s WHERE s.leaderboard = :leaderboard ORDER BY s.createdAt DESC"
  )
  List<LeaderboardSnapshotEntity> findLatestByLeaderboard(
    @Param("leaderboard") LeaderboardEntity leaderboard
  );

  @Query(
    "SELECT s.id as id, s.snapshotIdentifier as snapshotIdentifier, s.description as description, " +
    "s.createdAt as createdAt, COUNT(e) as entryCount " +
    "FROM LeaderboardSnapshotEntity s " +
    "LEFT JOIN LeaderboardEntryEntity e ON e.snapshot = s " +
    "WHERE s.leaderboard = :leaderboard " +
    "GROUP BY s.id, s.snapshotIdentifier, s.description, s.createdAt " +
    "ORDER BY s.createdAt DESC"
  )
  Page<SnapshotWithEntryCount> findSnapshotsWithEntryCountByLeaderboard(
    @Param("leaderboard") LeaderboardEntity leaderboard,
    Pageable pageable
  );
}
