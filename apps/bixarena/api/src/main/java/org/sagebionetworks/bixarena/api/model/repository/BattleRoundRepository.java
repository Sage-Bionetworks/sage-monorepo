package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRoundRepository extends JpaRepository<BattleRoundEntity, UUID> {
  Optional<BattleRoundEntity> findTopByBattleIdOrderByRoundNumberDesc(UUID battleId);

  List<BattleRoundEntity> findByBattleIdOrderByRoundNumberAsc(UUID battleId);

  @Modifying
  @Query("UPDATE BattleRoundEntity r SET r.model1MessageId = :msgId WHERE r.id = :roundId")
  void setModel1MessageId(@Param("roundId") UUID roundId, @Param("msgId") UUID msgId);

  @Modifying
  @Query("UPDATE BattleRoundEntity r SET r.model2MessageId = :msgId WHERE r.id = :roundId")
  void setModel2MessageId(@Param("roundId") UUID roundId, @Param("msgId") UUID msgId);
}
