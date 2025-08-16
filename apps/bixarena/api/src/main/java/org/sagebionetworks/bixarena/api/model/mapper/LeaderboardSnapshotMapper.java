package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;

public class LeaderboardSnapshotMapper {

  public LeaderboardSnapshotDto convertToDto(LeaderboardSnapshotEntity entity) {
    if (entity == null) {
      return null;
    }

    return LeaderboardSnapshotDto.builder()
      .id(entity.getSnapshotIdentifier())
      .createdAt(entity.getCreatedAt())
      .entryCount(0) // Will be set by service if needed
      .description(entity.getDescription())
      .build();
  }

  public List<LeaderboardSnapshotDto> convertToDtoList(List<LeaderboardSnapshotEntity> entities) {
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
