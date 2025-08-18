package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, UUID> {
  Optional<ModelEntity> findBySlug(String slug);
}
