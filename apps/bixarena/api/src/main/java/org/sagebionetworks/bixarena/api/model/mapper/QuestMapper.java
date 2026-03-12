package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;

public class QuestMapper {

  public QuestDto convertToDto(
      QuestEntity entity, List<QuestPostDto> posts, Integer totalBlocks) {
    if (entity == null) {
      return null;
    }

    return QuestDto.builder()
      .questId(entity.getQuestId())
      .title(entity.getTitle())
      .description(entity.getDescription())
      .goal(entity.getGoal())
      .startDate(entity.getStartDate())
      .endDate(entity.getEndDate())
      .activePostIndex(entity.getActivePostIndex())
      .totalBlocks(totalBlocks)
      .posts(posts)
      .build();
  }
}
