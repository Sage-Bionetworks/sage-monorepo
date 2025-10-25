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

    BattleEvaluationOutcomeDto outcomeDto = mapEntityOutcomeToDto(entity.getOutcome());

    return BattleEvaluationDto.builder()
      .id(entity.getId())
      .outcome(outcomeDto)
      .createdAt(entity.getCreatedAt())
      .isValid(entity.getIsValid())
      .validationError(entity.getValidationError())
      .build();
  }

  public List<BattleEvaluationDto> convertToDtoList(List<BattleEvaluationEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }

  private BattleEvaluationOutcomeDto mapEntityOutcomeToDto(BattleEvaluationEntity.Outcome outcome) {
    if (outcome == null) return null;
    return switch (outcome) {
      case MODEL_1 -> BattleEvaluationOutcomeDto.MODEL1;
      case MODEL_2 -> BattleEvaluationOutcomeDto.MODEL2;
      case TIE -> BattleEvaluationOutcomeDto.TIE;
    };
  }
}
