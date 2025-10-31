package org.sagebionetworks.bixarena.api.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.UserStatsDto;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
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

    // Calculate rank based on completed battles (includes users with 0 completed battles)
    Long rank = battleRepository.findUserRankByCompletedBattles(userId);

    // If rank is null, user has no battles yet - assign rank 1 (they're tied for first with 0 battles)
    if (rank == null) {
      rank = 1L;
      log.info("User {} has no battles yet, assigning default rank 1", userId);
    }

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
        .rank(rank)  // Always has a value (defaults to 1 for new users)
        .firstBattleAt(firstBattleAt)
        .latestBattleAt(latestBattleAt)
        .build();
  }
}
