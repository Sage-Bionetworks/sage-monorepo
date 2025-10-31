package org.sagebionetworks.bixarena.api.model.repository;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
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
}
