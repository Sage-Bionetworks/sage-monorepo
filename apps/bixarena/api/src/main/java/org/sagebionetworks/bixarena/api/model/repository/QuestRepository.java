package org.sagebionetworks.bixarena.api.model.repository;

import java.util.Optional;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends JpaRepository<QuestEntity, Long> {

  /**
   * Find a quest by its quest ID.
   *
   * @param questId the quest identifier
   * @return Optional containing the quest if found
   */
  Optional<QuestEntity> findByQuestId(String questId);
}
