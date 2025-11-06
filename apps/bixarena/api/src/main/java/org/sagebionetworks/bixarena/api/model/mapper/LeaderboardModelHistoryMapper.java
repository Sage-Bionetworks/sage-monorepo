package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.HistoricalLeaderboardEntryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;

public class LeaderboardModelHistoryMapper {

  public static HistoricalLeaderboardEntryDto toDto(LeaderboardEntryEntity entity) {
    if (entity == null) {
      return null;
    }

    return HistoricalLeaderboardEntryDto.builder()
      .snapshotId(entity.getSnapshot().getSnapshotIdentifier())
      .btScore(entity.getBtScore().doubleValue())
      .voteCount(entity.getVoteCount())
      .rank(entity.getRank())
      .bootstrapQ025(entity.getBootstrapQ025().doubleValue())
      .bootstrapQ975(entity.getBootstrapQ975().doubleValue())
      .createdAt(entity.getCreatedAt())
      .build();
  }

  public List<HistoricalLeaderboardEntryDto> convertToDtoList(
    List<LeaderboardEntryEntity> entities
  ) {
    return entities.stream().map(LeaderboardModelHistoryMapper::toDto).collect(Collectors.toList());
  }
}
