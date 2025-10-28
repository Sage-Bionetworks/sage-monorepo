package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationOutcomeDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEvaluationEntity;

/**
 * Simple mapper between {@link BattleEvaluationEntity} and {@link EvaluationDto}.
 */
public class BattleEvaluationMapper {

  public BattleEvaluationDto convertToDto(BattleEvaluationEntity entity) {
    if (entity == null) return null;

    BattleEvaluationOutcomeDto outcomeDto = BattleEvaluationOutcomeDto.fromValue(entity.getOutcome());

    return BattleEvaluationDto.builder()
      .id(entity.getId())
      .battleId(entity.getBattleId())
      .outcome(outcomeDto)
      .createdAt(entity.getCreatedAt())
      .valid(entity.getValid())
      .build();
  }

  public List<BattleEvaluationDto> convertToDtoList(List<BattleEvaluationEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }

}
