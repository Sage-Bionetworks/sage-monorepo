package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
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

  /**
   * Get public platform statistics.
   * This endpoint is publicly accessible without authentication.
   *
   * @return Public statistics response
   */
  @Override
  public ResponseEntity<PublicStatsDto> getPublicStats() {
    // Query actual user count from auth.user table
    Long totalUsers = userStatsRepository.count();

    // TODO: Replace remaining mock data with real queries
    PublicStatsDto stats = PublicStatsDto.builder()
        .modelsEvaluated(42L)
        .totalBattles(1337L)
        .totalUsers(totalUsers)
        .build();

    return ResponseEntity.ok(stats);
  }
}
