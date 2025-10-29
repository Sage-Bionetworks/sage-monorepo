package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.UserStatsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Stats API delegate.
 * Provides public statistics about the BixArena platform.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StatsApiDelegateImpl implements StatsApiDelegate {

  private final UserStatsRepository userStatsRepository;
  private final BattleRepository battleRepository;

  /**
   * Get public platform statistics.
   * This endpoint is publicly accessible without authentication.
   * Results are cached for 5 minutes to reduce database load.
   *
   * @return Public statistics response
   */
  @Override
  public ResponseEntity<PublicStatsDto> getPublicStats() {
    PublicStatsDto stats = getPublicStatsDto();
    return ResponseEntity.ok(stats);
  }

  /**
   * Get the cached public stats DTO.
   * This method is cacheable and returns only the DTO object.
   *
   * @return Public statistics DTO
   */
  @Cacheable(value = "publicStats", key = "'stats'")
  private PublicStatsDto getPublicStatsDto() {
    log.info("Cache miss - querying database for public stats");

    // These queries only execute on cache miss
    Long totalUsers = userStatsRepository.count();
    Long totalBattles = battleRepository.count();
    Long modelsEvaluated = battleRepository.countDistinctModelsEvaluated();

    PublicStatsDto stats =
        PublicStatsDto.builder()
            .modelsEvaluated(modelsEvaluated)
            .totalBattles(totalBattles)
            .totalUsers(totalUsers)
            .build();

    log.debug("Public stats computed: {}", stats);
    return stats;
  }
}
