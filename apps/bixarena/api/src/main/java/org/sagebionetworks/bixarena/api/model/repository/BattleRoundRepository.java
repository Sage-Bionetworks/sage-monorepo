package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRoundRepository extends JpaRepository<BattleRoundEntity, UUID> {
  Optional<BattleRoundEntity> findTopByBattleIdOrderByRoundNumberDesc(UUID battleId);
}
