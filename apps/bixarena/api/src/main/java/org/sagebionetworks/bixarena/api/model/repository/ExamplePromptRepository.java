package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamplePromptRepository
  extends JpaRepository<ExamplePromptEntity, UUID>, JpaSpecificationExecutor<ExamplePromptEntity> {
  @Query(value = "SELECT * FROM example_prompt ORDER BY RANDOM()", nativeQuery = true)
  Page<ExamplePromptEntity> findAllRandomly(Pageable pageable);
}
