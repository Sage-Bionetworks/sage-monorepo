package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;

public class BattleMapper {

  private final ModelMapper modelMapper = new ModelMapper();

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

  public BattleCreateResponseDto convertToCreateResponseDto(
    BattleEntity battle,
    ModelEntity model1Entity,
    ModelEntity model2Entity
  ) {
    if (battle == null) {
      return null;
    }

    return BattleCreateResponseDto.builder()
      .id(battle.getId())
      .title(battle.getTitle())
      .userId(battle.getUserId())
      .model1(modelMapper.convertToDto(model1Entity))
      .model2(modelMapper.convertToDto(model2Entity))
      .createdAt(battle.getCreatedAt())
      .endedAt(battle.getEndedAt())
      .build();
  }
}
