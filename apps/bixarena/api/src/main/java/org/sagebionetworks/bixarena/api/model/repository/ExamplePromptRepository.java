package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamplePromptRepository
  extends JpaRepository<ExamplePromptEntity, UUID>, JpaSpecificationExecutor<ExamplePromptEntity> {
  @Query(
    value = "SELECT * FROM example_prompt ep " +
    "WHERE ep.active = TRUE " +
    "ORDER BY random() LIMIT :page_size",
    nativeQuery = true
  )
  List<ExamplePromptEntity> findRandom(@Param("page_size") int pageSize);
}
