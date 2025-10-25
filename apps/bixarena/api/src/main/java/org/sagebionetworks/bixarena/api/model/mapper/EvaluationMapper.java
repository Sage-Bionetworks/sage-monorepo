package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationOutcomeDto;
import org.sagebionetworks.bixarena.api.model.entity.EvaluationEntity;

/**
 * Simple mapper between {@link EvaluationEntity} and {@link EvaluationDto}.
 */
public class EvaluationMapper {

  public EvaluationDto convertToDto(EvaluationEntity entity) {
    if (entity == null) return null;

    // Map enum values here so callers (services/controllers) receive a complete DTO
    EvaluationOutcomeDto outcomeDto = mapEntityOutcomeToDto(entity.getOutcome());

    return EvaluationDto.builder()
      .id(entity.getId())
      .outcome(outcomeDto)
      .createdAt(entity.getCreatedAt())
      .isValid(entity.getIsValid())
      .validationError(entity.getValidationError())
      .build();
  }

  public List<EvaluationDto> convertToDtoList(List<EvaluationEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }

  private EvaluationOutcomeDto mapEntityOutcomeToDto(EvaluationEntity.Outcome outcome) {
    if (outcome == null) return null;
    return switch (outcome) {
      case MODEL_1 -> EvaluationOutcomeDto.MODEL1;
      case MODEL_2 -> EvaluationOutcomeDto.MODEL2;
      case TIE -> EvaluationOutcomeDto.TIE;
    };
  }
}
