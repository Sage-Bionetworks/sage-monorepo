package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.projection.PromptBattleCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamplePromptRepository
  extends JpaRepository<ExamplePromptEntity, UUID>, JpaSpecificationExecutor<ExamplePromptEntity> {
  @Query(
    value = "SELECT * FROM api.example_prompt ep " +
    "WHERE ep.active = TRUE " +
    "ORDER BY random() LIMIT :pageSize",
    nativeQuery = true
  )
  List<ExamplePromptEntity> findRandom(@Param("pageSize") int pageSize);

  @Modifying
  @Query(
    value =
      "UPDATE api.example_prompt SET effective_categorization_id = :categorizationId " +
      "WHERE id = :promptId AND effective_categorization_id IS NULL",
    nativeQuery = true
  )
  int setEffectiveCategorizationIfNull(
    @Param("promptId") UUID promptId,
    @Param("categorizationId") UUID categorizationId
  );

  @Query(
    value =
      "SELECT b.example_prompt_id AS promptId, COUNT(b.id) AS battleCount " +
      "FROM api.battle b " +
      "JOIN api.battle_validation v " +
      "  ON v.id = b.effective_validation_id AND v.is_biomedical = TRUE " +
      "WHERE b.ended_at IS NOT NULL " +
      "  AND b.example_prompt_id IN (:promptIds) " +
      "GROUP BY b.example_prompt_id",
    nativeQuery = true
  )
  List<PromptBattleCountProjection> findBattleCountsByPromptIds(
    @Param("promptIds") Collection<UUID> promptIds
  );

  @Query(
    value =
      "SELECT b.example_prompt_id " +
      "FROM api.battle b " +
      "JOIN api.battle_validation v " +
      "  ON v.id = b.effective_validation_id AND v.is_biomedical = TRUE " +
      "JOIN api.example_prompt ep " +
      "  ON ep.id = b.example_prompt_id AND ep.active = TRUE " +
      "WHERE b.ended_at IS NOT NULL " +
      "  AND (:lookback = -1 OR b.ended_at > now() - make_interval(days => :lookback)) " +
      "GROUP BY b.example_prompt_id " +
      "ORDER BY COUNT(b.id) DESC " +
      "LIMIT :pageSize",
    nativeQuery = true
  )
  List<UUID> findTopActivePromptIdsByUsage(
    @Param("lookback") int lookback,
    @Param("pageSize") int pageSize
  );
}
