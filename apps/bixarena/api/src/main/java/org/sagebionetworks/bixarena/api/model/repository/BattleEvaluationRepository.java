package org.sagebionetworks.bixarena.api.model.repository;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.entity.BattleEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleEvaluationRepository extends JpaRepository<BattleEvaluationEntity, UUID> {}
