package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
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
    "ORDER BY random() LIMIT :page_size",
    nativeQuery = true
  )
  List<ExamplePromptEntity> findRandom(@Param("page_size") int pageSize);

  @Query(
    value = "SELECT ep.* FROM api.example_prompt ep " +
    "WHERE ep.active = TRUE AND EXISTS (" +
    "  SELECT 1 FROM api.example_prompt_categorization_category epcc " +
    "  WHERE epcc.categorization_id = ep.effective_categorization_id " +
    "    AND epcc.category IN (:slugs)" +
    ") " +
    "ORDER BY random() LIMIT :page_size",
    nativeQuery = true
  )
  List<ExamplePromptEntity> findRandomByCategory(
    @Param("slugs") Collection<String> slugs,
    @Param("page_size") int pageSize
  );

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
}
