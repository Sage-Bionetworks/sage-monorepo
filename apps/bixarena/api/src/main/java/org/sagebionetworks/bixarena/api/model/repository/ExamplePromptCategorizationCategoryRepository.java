package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationCategoryEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationCategoryEntity.CategoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamplePromptCategorizationCategoryRepository
    extends JpaRepository<ExamplePromptCategorizationCategoryEntity, CategoryKey> {
  List<ExamplePromptCategorizationCategoryEntity> findByCategorizationId(UUID categorizationId);
}
