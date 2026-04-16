package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleCategorizationCategoryEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleCategorizationCategoryEntity.CategoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleCategorizationCategoryRepository
    extends JpaRepository<BattleCategorizationCategoryEntity, CategoryKey> {
  List<BattleCategorizationCategoryEntity> findByCategorizationId(UUID categorizationId);
}
