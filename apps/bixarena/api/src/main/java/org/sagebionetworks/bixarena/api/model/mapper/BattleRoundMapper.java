package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;

public class BattleRoundMapper {

  public BattleRoundDto convertToDto(BattleRoundEntity entity) {
    if (entity == null) return null;

    return BattleRoundDto.builder()
      .id(entity.getId())
      .battleId(entity.getBattleId())
      .roundNumber(entity.getRoundNumber())
      .promptMessageId(entity.getPromptMessageId())
      .model1MessageId(entity.getModel1MessageId())
      .model2MessageId(entity.getModel2MessageId())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .build();
  }

  public List<BattleRoundDto> convertToDtoList(List<BattleRoundEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
