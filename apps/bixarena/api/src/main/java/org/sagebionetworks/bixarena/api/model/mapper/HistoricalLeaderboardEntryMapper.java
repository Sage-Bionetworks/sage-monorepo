package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.HistoricalLeaderboardEntryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;

public class HistoricalLeaderboardEntryMapper {

  public HistoricalLeaderboardEntryDto convertToDto(LeaderboardEntryEntity entity) {
    if (entity == null) {
      return null;
    }

    return HistoricalLeaderboardEntryDto.builder()
      .snapshotId(entity.getSnapshot().getSnapshotIdentifier())
      .btScore(entity.getBtScore().doubleValue())
      .voteCount(entity.getVoteCount())
      .rank(entity.getRank())
      .createdAt(entity.getCreatedAt())
      .secondaryScore(
        entity.getSecondaryScore() != null ? entity.getSecondaryScore().doubleValue() : null
      )
      .build();
  }

  public List<HistoricalLeaderboardEntryDto> convertToDtoList(
    List<LeaderboardEntryEntity> entities
  ) {
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
