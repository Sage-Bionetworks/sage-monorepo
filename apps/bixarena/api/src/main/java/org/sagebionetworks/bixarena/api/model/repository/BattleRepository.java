package org.sagebionetworks.bixarena.api.model.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.projection.ContributorProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRepository
    extends JpaRepository<BattleEntity, UUID>, JpaSpecificationExecutor<BattleEntity> {

  /**
   * Count distinct models that have been evaluated in battles. This counts unique models that
   * appear as either model1 or model2 in battles that have a BattleEvaluation record.
   *
   * @return Count of unique models evaluated
   */
  @Query(
      value =
          "SELECT COUNT(DISTINCT model_id) FROM ("
              + "SELECT b.model1_id AS model_id "
              + "FROM api.battle b "
              + "INNER JOIN api.battle_evaluation be ON b.id = be.battle_id "
              + "UNION "
              + "SELECT b.model2_id AS model_id "
              + "FROM api.battle b "
              + "INNER JOIN api.battle_evaluation be ON b.id = be.battle_id"
              + ") AS evaluated_models",
      nativeQuery = true)
  Long countDistinctModelsEvaluated();

  /**
   * Count all completed battles (with ended_at set).
   *
   * @return Total number of completed battles
   */
  @Query("SELECT COUNT(b) FROM BattleEntity b WHERE b.endedAt IS NOT NULL")
  Long countCompleted();

  /**
   * Count total battles created by a specific user.
   *
   * @param userId The user's UUID
   * @return Total number of battles created by the user
   */
  @Query("SELECT COUNT(b) FROM BattleEntity b WHERE b.userId = :userId")
  Long countByUserId(@Param("userId") UUID userId);

  /**
   * Count completed battles (with ended_at set) created by a specific user.
   *
   * @param userId The user's UUID
   * @return Number of completed battles
   */
  @Query("SELECT COUNT(b) FROM BattleEntity b WHERE b.userId = :userId AND b.endedAt IS NOT NULL")
  Long countCompletedByUserId(@Param("userId") UUID userId);

  /**
   * Count active battles (ended_at is null) created by a specific user.
   *
   * @param userId The user's UUID
   * @return Number of active battles
   */
  @Query("SELECT COUNT(b) FROM BattleEntity b WHERE b.userId = :userId AND b.endedAt IS NULL")
  Long countActiveByUserId(@Param("userId") UUID userId);

  /**
   * Get the timestamp of the first battle created by a specific user.
   *
   * @param userId The user's UUID
   * @return Timestamp of first battle, or null if no battles exist
   */
  @Query(
      "SELECT MIN(b.createdAt) FROM BattleEntity b WHERE b.userId = :userId"
  )
  OffsetDateTime findFirstBattleTimestampByUserId(@Param("userId") UUID userId);

  /**
   * Get the timestamp of the most recent battle created by a specific user.
   *
   * @param userId The user's UUID
   * @return Timestamp of latest battle, or null if no battles exist
   */
  @Query(
      "SELECT MAX(b.createdAt) FROM BattleEntity b WHERE b.userId = :userId"
  )
  OffsetDateTime findLatestBattleTimestampByUserId(@Param("userId") UUID userId);

  /**
   * Calculate user's rank based on completed battles using standard competition ranking.
   * Users with the same number of completed battles share the same rank.
   * All authenticated users have a rank, including those with 0 completed battles.
   *
   * @param userId The user ID to calculate rank for
   * @return The user's rank (never null for authenticated users)
   */
  @Query(
    value =
      "WITH user_battle_counts AS ( " +
      "  SELECT " +
      "    u.id as user_id, " +
      "    COUNT(CASE WHEN b.ended_at IS NOT NULL THEN 1 END) as completed_battles " +
      "  FROM auth.user u " +
      "  LEFT JOIN api.battle b ON u.id = b.user_id " +
      "  GROUP BY u.id " +
      "), " +
      "ranked_users AS ( " +
      "  SELECT " +
      "    user_id, " +
      "    completed_battles, " +
      "    RANK() OVER (ORDER BY completed_battles DESC) as rank " +
      "  FROM user_battle_counts " +
      ") " +
      "SELECT rank " +
      "FROM ranked_users " +
      "WHERE user_id = :userId",
    nativeQuery = true
  )
  Long findUserRankByCompletedBattles(@Param("userId") UUID userId);

  /**
   * Find contributors to a quest by counting their completed battles within a date range.
   * Returns username and battle count, ordered by battle count descending, then username ascending.
   * Uses native query for better performance with GROUP BY and JOIN to auth.user.
   *
   * @param startDate quest start date
   * @param endDate quest end date
   * @param minBattles minimum battles to be included
   * @param pageable pagination information (for limit)
   * @return List of contributors with their battle counts
   */
  @Query(
    value =
      "SELECT " +
      "  u.username AS username, " +
      "  CAST(COUNT(b.id) AS INTEGER) AS battleCount " +
      "FROM api.battle b " +
      "INNER JOIN auth.user u ON b.user_id = u.id " +
      "WHERE b.ended_at IS NOT NULL " +
      "  AND b.ended_at >= :startDate " +
      "  AND b.ended_at < :endDate " +
      "GROUP BY u.id, u.username " +
      "HAVING COUNT(b.id) >= :minBattles " +
      "ORDER BY COUNT(b.id) DESC, u.username ASC",
    nativeQuery = true
  )
  List<ContributorProjection> findContributorsByDateRange(
      @Param("startDate") OffsetDateTime startDate,
      @Param("endDate") OffsetDateTime endDate,
      @Param("minBattles") int minBattles,
      Pageable pageable);
}
