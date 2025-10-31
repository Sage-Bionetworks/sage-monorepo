package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository
  extends JpaRepository<ModelEntity, UUID>, JpaSpecificationExecutor<ModelEntity> {
  Optional<ModelEntity> findBySlug(String slug);

  @Query(
    value = "SELECT * FROM api.model m " + "WHERE m.active = TRUE " + "ORDER BY random() LIMIT :count",
    nativeQuery = true
  )
  List<ModelEntity> findRandomActiveModels(@Param("count") int count);
}
