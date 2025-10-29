package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
import org.sagebionetworks.bixarena.api.service.PublicStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Stats API delegate.
 * Provides public statistics about the BixArena platform.
 */
@Component
@RequiredArgsConstructor
public class StatsApiDelegateImpl implements StatsApiDelegate {

  private final PublicStatsService publicStatsService;

  /**
   * Get public platform statistics.
   * This endpoint is publicly accessible without authentication.
   * Results are cached for 5 minutes to reduce database load.
   *
   * @return Public statistics response
   */
  @Override
  public ResponseEntity<PublicStatsDto> getPublicStats() {
    PublicStatsDto stats = publicStatsService.getPublicStats();
    return ResponseEntity.ok(stats);
  }
}
