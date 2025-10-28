package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.UserStatsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Stats API delegate.
 * Provides public statistics about the BixArena platform.
 */
@Component
@RequiredArgsConstructor
public class StatsApiDelegateImpl implements StatsApiDelegate {

  private final UserStatsRepository userStatsRepository;
  private final BattleRepository battleRepository;

  /**
   * Get public platform statistics.
   * This endpoint is publicly accessible without authentication.
   *
   * @return Public statistics response
   */
  @Override
  public ResponseEntity<PublicStatsDto> getPublicStats() {
    // Query actual counts from database
    Long totalUsers = userStatsRepository.count();
    Long totalBattles = battleRepository.count();
    Long modelsEvaluated = battleRepository.countDistinctModelsEvaluated();

    PublicStatsDto stats = PublicStatsDto.builder()
        .modelsEvaluated(modelsEvaluated)
        .totalBattles(totalBattles)
        .totalUsers(totalUsers)
        .build();

    return ResponseEntity.ok(stats);
  }
}
