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
      .id(entity.getId().toString())
      .title(entity.getTitle())
      .userId(entity.getUserId().toString())
      .leftModelId(entity.getLeftModelId().toString())
      .rightModelId(entity.getRightModelId().toString())
      .createdAt(entity.getCreatedAt())
      .endedAt(entity.getEndedAt())
      .build();
  }

  public List<BattleDto> convertToDtoList(List<BattleEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
