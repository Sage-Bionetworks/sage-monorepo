package org.sagebionetworks.bixarena.api.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.sagebionetworks.bixarena.api.model.dto.UserStatsDto;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving user-specific battle statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsService {

  private final BattleRepository battleRepository;

  /**
   * Get user's rank based on completed battles.
   * This method is cached with a 5-minute TTL to reduce database load.
   *
   * @param userId The user's UUID
   * @return User's rank (never null for authenticated users)
   */
  @Cacheable(value = CacheNames.USER_RANKS, key = "#userId")
  @Transactional(readOnly = true)
  public Long getUserRank(UUID userId) {
    log.info("Cache miss - calculating rank for user: {}", userId);
    Long rank = battleRepository.findUserRankByCompletedBattles(userId);
    log.info("User {} rank: {}", userId, rank);
    return rank;
  }

  /**
   * Get battle statistics for a specific user.
   *
   * @param userId The user's UUID from JWT token
   * @return User statistics DTO
   */
  @Transactional(readOnly = true)
  public UserStatsDto getUserStats(UUID userId) {
    log.info("Fetching battle statistics for user: {}", userId);

    // Query all stats in parallel
    Long totalBattles = battleRepository.countByUserId(userId);
    Long completedBattles = battleRepository.countCompletedByUserId(userId);
    Long activeBattles = battleRepository.countActiveByUserId(userId);
    OffsetDateTime firstBattleAt = battleRepository.findFirstBattleTimestampByUserId(userId);
    OffsetDateTime latestBattleAt = battleRepository.findLatestBattleTimestampByUserId(userId);

    // Get cached rank (or calculate if cache miss)
    Long rank = getUserRank(userId);

    log.info(
        "User {} stats: total={}, completed={}, active={}, rank={}, first={}, latest={}",
        userId,
        totalBattles,
        completedBattles,
        activeBattles,
        rank,
        firstBattleAt,
        latestBattleAt
    );

    return UserStatsDto.builder()
        .totalBattles(totalBattles != null ? totalBattles : 0L)
        .completedBattles(completedBattles != null ? completedBattles : 0L)
        .activeBattles(activeBattles != null ? activeBattles : 0L)
        .rank(rank)  // Never null - all authenticated users are ranked
        .firstBattleAt(firstBattleAt)
        .latestBattleAt(latestBattleAt)
        .build();
  }
}
