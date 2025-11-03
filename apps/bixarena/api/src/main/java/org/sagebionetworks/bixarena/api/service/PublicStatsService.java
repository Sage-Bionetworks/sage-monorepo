package org.sagebionetworks.bixarena.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.sagebionetworks.bixarena.api.model.dto.PublicStatsDto;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.UserStatsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for retrieving public platform statistics.
 * Results are cached to reduce database load.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PublicStatsService {

  private final UserStatsRepository userStatsRepository;
  private final BattleRepository battleRepository;

  /**
   * Get public platform statistics.
   * This method is cacheable and returns the DTO object directly.
   *
   * @return Public statistics DTO
   */
  @Cacheable(value = CacheNames.PUBLIC_STATS, key = "'" + CacheNames.PUBLIC_STATS_KEY + "'")
  public PublicStatsDto getPublicStats() {
    log.info("Cache miss - querying database for public stats");

    // These queries only execute on cache miss
    Long totalUsers = userStatsRepository.count();
    Long completedBattles = battleRepository.countCompleted();
    Long modelsEvaluated = battleRepository.countDistinctModelsEvaluated();

    PublicStatsDto stats =
        PublicStatsDto.builder()
            .modelsEvaluated(modelsEvaluated)
            .completedBattles(completedBattles)
            .totalUsers(totalUsers)
            .build();

    log.debug("Public stats computed: {}", stats);
    return stats;
  }
}
