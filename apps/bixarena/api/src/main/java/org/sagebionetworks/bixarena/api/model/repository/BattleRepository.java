package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
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
}
