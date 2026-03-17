package org.sagebionetworks.bixarena.api.model.repository;

import java.util.List;
import java.util.Optional;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestPostRepository extends JpaRepository<QuestPostEntity, Long> {

  List<QuestPostEntity> findByQuestIdOrderByPostIndexAsc(Long questId);

  Optional<QuestPostEntity> findByQuestIdAndPostIndex(Long questId, Integer postIndex);

  void deleteByQuestId(Long questId);

  @Query("SELECT COALESCE(MAX(p.postIndex), -1) FROM QuestPostEntity p WHERE p.questId = :questId")
  Integer findMaxPostIndex(@Param("questId") Long questId);
}
