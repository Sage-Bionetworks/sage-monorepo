package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardEntryRepository
  extends
    JpaRepository<LeaderboardEntryEntity, UUID>,
    JpaSpecificationExecutor<LeaderboardEntryEntity> {

  interface RankBySlug {
    String getModelSlug();
    Integer getRank();
  }

  long countBySnapshot(LeaderboardSnapshotEntity snapshot);

  @Query(
    "SELECT COALESCE(SUM(e.voteCount), 0) FROM LeaderboardEntryEntity e WHERE e.snapshot = :snapshot"
  )
  long sumVoteCountBySnapshot(@Param("snapshot") LeaderboardSnapshotEntity snapshot);

  Page<LeaderboardEntryEntity> findByLeaderboardAndModel(
    LeaderboardEntity leaderboard,
    ModelEntity model,
    Pageable pageable
  );

  @Query(
    "SELECT e.model.slug AS modelSlug, e.rank AS rank " +
    "FROM LeaderboardEntryEntity e " +
    "WHERE e.snapshot.id = :snapshotId"
  )
  List<RankBySlug> findRanksBySnapshotId(@Param("snapshotId") UUID snapshotId);
}
