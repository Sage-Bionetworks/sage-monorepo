package org.sagebionetworks.bixarena.api.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.QuestNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorDto.TierEnum;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.projection.ContributorProjection;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing community quests and their contributors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestService {

  private final QuestRepository questRepository;
  private final BattleRepository battleRepository;

  // Tier thresholds (battles per week)
  private static final double CHAMPION_THRESHOLD = 10.0;
  private static final double KNIGHT_THRESHOLD = 5.0;

  /**
   * Get contributors for a specific quest.
   *
   * @param questId the quest identifier
   * @param minBattles minimum battles required to be listed
   * @param limit maximum number of contributors to return
   * @return quest contributors data
   * @throws QuestNotFoundException if quest not found
   */
  @Transactional(readOnly = true)
  public QuestContributorsDto getQuestContributors(
      String questId, int minBattles, int limit) {
    log.debug("Fetching contributors for quest: {}", questId);

    // Fetch quest configuration
    QuestEntity quest =
        questRepository
            .findByQuestId(questId)
            .orElseThrow(
                () -> new QuestNotFoundException("Quest not found: " + questId));

    // Query contributors
    List<ContributorProjection> contributorProjections =
        battleRepository.findContributorsByDateRange(
            quest.getStartDate(), quest.getEndDate(), minBattles, PageRequest.of(0, limit));

    // Calculate tiers and battles per week for each contributor
    // Use quest end date if quest has ended, otherwise use current time
    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime effectiveEndDate =
        now.isAfter(quest.getEndDate()) ? quest.getEndDate() : now;
    double questWeeks = calculateWeeks(quest.getStartDate(), effectiveEndDate);

    // Convert projections to DTOs with tier information
    List<QuestContributorDto> contributors =
        contributorProjections.stream()
            .map(
                projection -> {
                  double battlesPerWeek = projection.getBattleCount() / questWeeks;
                  TierEnum tier = calculateTier(battlesPerWeek);

                  return QuestContributorDto.builder()
                      .username(projection.getUsername())
                      .battleCount(projection.getBattleCount())
                      .tier(tier)
                      .battlesPerWeek(battlesPerWeek)
                      .build();
                })
            .collect(Collectors.toList());

    log.info("Found {} contributors for quest: {}", contributors.size(), questId);

    return QuestContributorsDto.builder()
        .questId(quest.getQuestId())
        .startDate(quest.getStartDate())
        .endDate(quest.getEndDate())
        .totalContributors(contributors.size())
        .contributors(contributors)
        .build();
  }

  /**
   * Calculate the number of weeks between two dates.
   *
   * @param startDate quest start date
   * @param endDate quest end date (or current date)
   * @return number of weeks as a double
   */
  private double calculateWeeks(OffsetDateTime startDate, OffsetDateTime endDate) {
    Duration duration = Duration.between(startDate, endDate);
    long days = duration.toDays();
    // Ensure minimum of 1 day to avoid division by zero
    if (days < 1) {
      days = 1;
    }
    return days / 7.0;
  }

  /**
   * Calculate contributor tier based on battles per week.
   *
   * <p>Tier thresholds: - Champions (🏆): ≥10 battles/week - Knights (⚔️): ≥5 battles/week -
   * Apprentices (🌟): <5 battles/week
   *
   * @param battlesPerWeek average battles per week
   * @return contributor tier
   */
  private TierEnum calculateTier(double battlesPerWeek) {
    if (battlesPerWeek >= CHAMPION_THRESHOLD) {
      return TierEnum.CHAMPION;
    } else if (battlesPerWeek >= KNIGHT_THRESHOLD) {
      return TierEnum.KNIGHT;
    } else {
      return TierEnum.APPRENTICE;
    }
  }
}
