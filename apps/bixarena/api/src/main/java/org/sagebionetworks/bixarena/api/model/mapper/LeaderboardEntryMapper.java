package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;

public class LeaderboardEntryMapper {

  public LeaderboardEntryDto convertToDto(LeaderboardEntryEntity entity) {
    if (entity == null) {
      return null;
    }

    return LeaderboardEntryDto.builder()
      .id(entity.getId().toString())
      .modelId(entity.getModel().getSlug())
      .modelName(entity.getModel().getName())
      .license(entity.getModel().getLicense())
      .btScore(entity.getBtScore().doubleValue())
      .evaluationCount(entity.getEvaluationCount())
      .rank(entity.getRank())
      .createdAt(entity.getCreatedAt())
      .build();
  }

  public List<LeaderboardEntryDto> convertToDtoList(List<LeaderboardEntryEntity> entities) {
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
