package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Stats API delegate.
 * Provides public statistics about the BixArena platform.
 */
@Component
public class StatsApiDelegateImpl implements StatsApiDelegate {

  /**
   * Get public platform statistics.
   * This endpoint is publicly accessible without authentication.
   * Currently returns mock data.
   *
   * @return Public statistics response
   */
  @Override
  public ResponseEntity<PublicStatsDto> getPublicStats() {
    PublicStatsDto stats = PublicStatsDto.builder()
        .modelsEvaluated(42L)
        .totalBattles(1337L)
        .totalUsers(256L)
        .build();

    return ResponseEntity.ok(stats);
  }
}
