package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;

public class BattleMapper {

  public BattleDto convertToDto(BattleEntity entity) {
    if (entity == null) {
      return null;
    }

    return BattleDto.builder()
      .id(entity.getId())
      .title(entity.getTitle())
      .userId(entity.getUserId())
      .model1Id(entity.getModel1Id())
      .model2Id(entity.getModel2Id())
      .createdAt(entity.getCreatedAt())
      .endedAt(entity.getEndedAt())
      .build();
  }

  public List<BattleDto> convertToDtoList(List<BattleEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
