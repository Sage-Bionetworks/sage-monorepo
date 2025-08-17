package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.projection.SnapshotWithEntryCount;

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

  public LeaderboardSnapshotDto convertToDtoWithEntryCount(LeaderboardSnapshotEntity entity, long entryCount) {
    if (entity == null) {
      return null;
    }

    return LeaderboardSnapshotDto.builder()
      .id(entity.getSnapshotIdentifier())
      .createdAt(entity.getCreatedAt())
      .entryCount((int) entryCount)
      .description(entity.getDescription())
      .build();
  }

  public LeaderboardSnapshotDto convertFromProjection(SnapshotWithEntryCount projection) {
    if (projection == null) {
      return null;
    }

    return LeaderboardSnapshotDto.builder()
      .id(projection.getSnapshotIdentifier())
      .createdAt(projection.getCreatedAt())
      .entryCount(projection.getEntryCount().intValue())
      .description(projection.getDescription())
      .build();
  }

  public List<LeaderboardSnapshotDto> convertFromProjectionList(List<SnapshotWithEntryCount> projections) {
    return projections.stream().map(this::convertFromProjection).collect(Collectors.toList());
  }

  public List<LeaderboardSnapshotDto> convertToDtoList(List<LeaderboardSnapshotEntity> entities) {
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
