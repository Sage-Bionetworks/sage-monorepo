package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleValidationRepository extends JpaRepository<BattleValidationEntity, UUID> {
  List<BattleValidationEntity> findByBattleIdOrderByCreatedAtDesc(UUID battleId);
}
