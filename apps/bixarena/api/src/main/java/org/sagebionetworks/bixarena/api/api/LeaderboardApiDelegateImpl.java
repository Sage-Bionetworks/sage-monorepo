package org.sagebionetworks.bixarena.api.api;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardListInnerDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotQueryDto;
import org.sagebionetworks.bixarena.api.service.LeaderboardModelHistoryService;
import org.sagebionetworks.bixarena.api.service.LeaderboardService;
import org.sagebionetworks.bixarena.api.service.LeaderboardSnapshotService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardApiDelegateImpl implements LeaderboardApiDelegate {

  private final LeaderboardService leaderboardService;
  private final LeaderboardModelHistoryService leaderboardModelHistoryService;
  private final LeaderboardSnapshotService leaderboardSnapshotService;

  public LeaderboardApiDelegateImpl(
    LeaderboardService leaderboardService,
    LeaderboardModelHistoryService leaderboardModelHistoryService,
    LeaderboardSnapshotService leaderboardSnapshotService
  ) {
    this.leaderboardService = leaderboardService;
    this.leaderboardModelHistoryService = leaderboardModelHistoryService;
    this.leaderboardSnapshotService = leaderboardSnapshotService;
  }

  @Override
  public ResponseEntity<LeaderboardEntryPageDto> getLeaderboard(
    String leaderboardId,
    LeaderboardSearchQueryDto leaderboardSearchQuery
  ) {
    return ResponseEntity.ok(
      leaderboardService.getLeaderboard(leaderboardId, leaderboardSearchQuery)
    );
  }

  @Override
  public ResponseEntity<LeaderboardSnapshotPageDto> getLeaderboardSnapshots(
    String leaderboardId,
    LeaderboardSnapshotQueryDto leaderboardSnapshotQuery
  ) {
    return ResponseEntity.ok(
      leaderboardSnapshotService.getLeaderboardSnapshots(leaderboardId, leaderboardSnapshotQuery)
    );
  }

  @Override
  public ResponseEntity<LeaderboardModelHistoryPageDto> getModelHistory(
    String leaderboardId,
    String modelId,
    LeaderboardModelHistoryQueryDto leaderboardModelHistoryQuery
  ) {
    return ResponseEntity.ok(
      leaderboardModelHistoryService.getModelHistory(
        leaderboardId,
        modelId,
        leaderboardModelHistoryQuery
      )
    );
  }

  @Override
  public ResponseEntity<List<LeaderboardListInnerDto>> listLeaderboards() {
    return ResponseEntity.ok(leaderboardService.listLeaderboards());
  }
}
