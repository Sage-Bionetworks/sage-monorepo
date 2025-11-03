package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.UUID;
import org.sagebionetworks.bixarena.api.model.dto.ModelErrorCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelErrorDto;
import org.sagebionetworks.bixarena.api.model.entity.ModelErrorEntity;

public class ModelErrorMapper {

  private static final int MAX_ERROR_MESSAGE_LENGTH = 1000;

  /**
   * Truncates the error message to the maximum allowed length.
   *
   * @param errorMessage the error message to truncate
   * @return truncated error message
   */
  private String truncateErrorMessage(String errorMessage) {
    if (errorMessage == null) {
      return null;
    }
    if (errorMessage.length() <= MAX_ERROR_MESSAGE_LENGTH) {
      return errorMessage;
    }
    return errorMessage.substring(0, MAX_ERROR_MESSAGE_LENGTH);
  }

  public ModelErrorEntity convertToEntity(UUID modelId, ModelErrorCreateRequestDto dto) {
    if (dto == null) {
      return null;
    }

    return ModelErrorEntity.builder()
      .modelId(modelId)
      .errorCode(dto.getErrorCode())
      .errorMessage(truncateErrorMessage(dto.getErrorMessage()))
      .battleId(dto.getBattleId())
      .roundId(dto.getRoundId())
      .build();
  }

  public ModelErrorDto convertToDto(ModelErrorEntity entity) {
    if (entity == null) {
      return null;
    }

    return ModelErrorDto.builder()
      .id(entity.getId())
      .modelId(entity.getModelId())
      .errorCode(entity.getErrorCode())
      .errorMessage(entity.getErrorMessage())
      .battleId(entity.getBattleId())
      .roundId(entity.getRoundId())
      .createdAt(entity.getCreatedAt())
      .build();
  }
}
