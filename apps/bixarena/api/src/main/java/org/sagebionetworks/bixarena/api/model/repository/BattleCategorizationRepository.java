package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleCategorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleCategorizationRepository
    extends JpaRepository<BattleCategorizationEntity, UUID> {
  List<BattleCategorizationEntity> findByBattleIdOrderByCreatedAtDesc(UUID battleId);
}
