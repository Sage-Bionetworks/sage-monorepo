package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamplePromptCategorizationRepository
    extends JpaRepository<ExamplePromptCategorizationEntity, UUID> {
  List<ExamplePromptCategorizationEntity> findByPromptIdOrderByCreatedAtDesc(UUID promptId);
}
