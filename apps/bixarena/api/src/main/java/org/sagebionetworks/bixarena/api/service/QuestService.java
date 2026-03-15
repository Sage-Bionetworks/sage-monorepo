package org.sagebionetworks.bixarena.api.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.sagebionetworks.bixarena.api.exception.DuplicateQuestException;
import org.sagebionetworks.bixarena.api.exception.QuestNotFoundException;
import org.sagebionetworks.bixarena.api.exception.QuestPostNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorDto.TierEnum;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostReorderDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.sagebionetworks.bixarena.api.model.mapper.QuestMapper;
import org.sagebionetworks.bixarena.api.model.mapper.QuestPostMapper;
import org.sagebionetworks.bixarena.api.model.projection.ContributorProjection;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestPostRepository;
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
  private final QuestPostRepository questPostRepository;
  private final BattleRepository battleRepository;

  private final QuestMapper questMapper = new QuestMapper();
  private final QuestPostMapper questPostMapper = new QuestPostMapper();

  // Tier thresholds (battles per week)
  private static final double CHAMPION_THRESHOLD = 10.0;
  private static final double KNIGHT_THRESHOLD = 5.0;

  // Tier hierarchy for comparison: champion > knight > public
  private static final List<String> TIER_HIERARCHY = List.of("champion", "knight");

  /**
   * Create a new quest.
   */
  @Transactional
  public QuestDto createQuest(QuestCreateOrUpdateDto dto) {
    if (questRepository.findByQuestId(dto.getQuestId()).isPresent()) {
      throw new DuplicateQuestException(dto.getQuestId());
    }

    QuestEntity entity = QuestEntity.builder()
      .questId(dto.getQuestId())
      .title(dto.getTitle())
      .description(dto.getDescription())
      .goal(dto.getGoal())
      .startDate(dto.getStartDate())
      .endDate(dto.getEndDate())
      .activePostIndex(dto.getActivePostIndex())
      .build();

    entity = questRepository.save(entity);
    log.info("Created quest: {}", entity.getQuestId());

    return questMapper.convertToDto(entity, List.of(), 0);
  }

  /**
   * Get a quest with posts filtered by unlock gates.
   * Gating is always applied, regardless of the caller's role.
   *
   * @param questId the quest identifier
   * @param userId authenticated user's UUID, or null for anonymous
   * @return quest with filtered posts
   */
  @Transactional(readOnly = true)
  public QuestDto getQuest(String questId, UUID userId) {
    QuestEntity quest = findQuestByQuestId(questId);

    List<QuestPostEntity> allPosts =
        questPostRepository.findByQuestIdOrderByPostIndexAsc(quest.getId());

    Integer totalBlocks = countTotalBlocks(quest);

    // Resolve caller's tier
    String callerTier = resolveTier(userId, quest);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    List<QuestPostDto> postDtos = allPosts.stream()
        .filter(post -> isPublished(post, now))
        .map(post -> {
          if (isUnlocked(post, totalBlocks, callerTier)) {
            return questPostMapper.convertToDto(post);
          } else {
            return questPostMapper.convertToLockedDto(post);
          }
        })
        .toList();

    return questMapper.convertToDto(quest, postDtos, totalBlocks);
  }

  /**
   * Get a quest with ALL posts ungated. For admin content management only.
   *
   * @param questId the quest identifier
   * @return quest with all posts and full content
   */
  @Transactional(readOnly = true)
  public QuestDto getQuestUngated(String questId) {
    QuestEntity quest = findQuestByQuestId(questId);

    List<QuestPostEntity> allPosts =
        questPostRepository.findByQuestIdOrderByPostIndexAsc(quest.getId());

    Integer totalBlocks = countTotalBlocks(quest);

    List<QuestPostDto> postDtos = allPosts.stream()
        .map(questPostMapper::convertToDto)
        .toList();

    return questMapper.convertToDto(quest, postDtos, totalBlocks);
  }

  /**
   * Update quest metadata.
   */
  @Transactional
  public QuestDto updateQuest(String questId, QuestCreateOrUpdateDto dto) {
    QuestEntity quest = findQuestByQuestId(questId);

    // If questId is being changed, check for duplicates
    if (!quest.getQuestId().equals(dto.getQuestId())
        && questRepository.findByQuestId(dto.getQuestId()).isPresent()) {
      throw new DuplicateQuestException(dto.getQuestId());
    }

    quest.setQuestId(dto.getQuestId());
    quest.setTitle(dto.getTitle());
    quest.setDescription(dto.getDescription());
    quest.setGoal(dto.getGoal());
    quest.setStartDate(dto.getStartDate());
    quest.setEndDate(dto.getEndDate());
    quest.setActivePostIndex(dto.getActivePostIndex());

    quest = questRepository.save(quest);
    log.info("Updated quest: {}", quest.getQuestId());

    List<QuestPostEntity> posts =
        questPostRepository.findByQuestIdOrderByPostIndexAsc(quest.getId());
    List<QuestPostDto> postDtos = posts.stream()
        .map(questPostMapper::convertToDto)
        .toList();

    return questMapper.convertToDto(quest, postDtos, countTotalBlocks(quest));
  }

  /**
   * Delete a quest and all its posts.
   */
  @Transactional
  public void deleteQuest(String questId) {
    QuestEntity quest = findQuestByQuestId(questId);
    questPostRepository.deleteByQuestId(quest.getId());
    questRepository.delete(quest);
    log.info("Deleted quest: {}", questId);
  }

  /**
   * Create a new post appended at the next available index.
   */
  @Transactional
  public QuestPostDto createQuestPost(String questId, QuestPostCreateOrUpdateDto dto) {
    QuestEntity quest = findQuestByQuestId(questId);

    Integer maxIndex = questPostRepository.findMaxPostIndex(quest.getId());
    int nextIndex = maxIndex + 1;

    QuestPostEntity entity = questPostMapper.convertToEntity(dto, quest.getId(), nextIndex);
    entity = questPostRepository.save(entity);
    log.info("Created post {} for quest {}", nextIndex, questId);

    return questPostMapper.convertToDto(entity);
  }

  /**
   * Update an existing quest post.
   */
  @Transactional
  public QuestPostDto updateQuestPost(
      String questId, Integer postIndex, QuestPostCreateOrUpdateDto dto) {
    QuestEntity quest = findQuestByQuestId(questId);

    QuestPostEntity post = questPostRepository
        .findByQuestIdAndPostIndex(quest.getId(), postIndex)
        .orElseThrow(() -> new QuestPostNotFoundException(
            String.format("Post %d not found in quest '%s'", postIndex, questId)));

    questPostMapper.updateEntity(post, dto);
    post = questPostRepository.save(post);
    log.info("Updated post {} in quest {}", postIndex, questId);

    return questPostMapper.convertToDto(post);
  }

  /**
   * Delete a quest post.
   */
  @Transactional
  public void deleteQuestPost(String questId, Integer postIndex) {
    QuestEntity quest = findQuestByQuestId(questId);

    QuestPostEntity post = questPostRepository
        .findByQuestIdAndPostIndex(quest.getId(), postIndex)
        .orElseThrow(() -> new QuestPostNotFoundException(
            String.format("Post %d not found in quest '%s'", postIndex, questId)));

    questPostRepository.delete(post);
    log.info("Deleted post {} from quest {}", postIndex, questId);

    if (quest.getActivePostIndex() != null && quest.getActivePostIndex().equals(postIndex)) {
      Integer maxIndex = questPostRepository.findMaxPostIndex(quest.getId());
      quest.setActivePostIndex(maxIndex != null && maxIndex >= 0 ? maxIndex : 0);
      questRepository.save(quest);
      log.info("Reset activePostIndex to {} for quest {}", quest.getActivePostIndex(), questId);
    }
  }

  /**
   * Reorder all posts in a quest.
   */
  @Transactional
  public QuestDto reorderQuestPosts(String questId, QuestPostReorderDto dto) {
    QuestEntity quest = findQuestByQuestId(questId);

    List<QuestPostEntity> existingPosts =
        questPostRepository.findByQuestIdOrderByPostIndexAsc(quest.getId());

    Set<Integer> existingIndexes = existingPosts.stream()
        .map(QuestPostEntity::getPostIndex)
        .collect(Collectors.toSet());

    List<Integer> requestedIndexes = dto.getPostIndexes();

    // Validate: same set of indexes, no duplicates
    Set<Integer> requestedSet = new HashSet<>(requestedIndexes);
    if (requestedSet.size() != requestedIndexes.size()) {
      throw new IllegalArgumentException("Duplicate post indexes in reorder request");
    }
    if (!requestedSet.equals(existingIndexes)) {
      throw new IllegalArgumentException(
          "Reorder request must contain exactly all existing post indexes");
    }

    // First pass: assign temporary negative indexes to avoid unique constraint violations
    for (int i = 0; i < existingPosts.size(); i++) {
      QuestPostEntity post = existingPosts.get(i);
      post.setPostIndex(-(post.getPostIndex() + 1));
    }
    questPostRepository.saveAll(existingPosts);
    questPostRepository.flush();

    // Build a map from old index to entity
    java.util.Map<Integer, QuestPostEntity> indexToPost = existingPosts.stream()
        .collect(Collectors.toMap(
            p -> -(p.getPostIndex() + 1),  // recover original index
            p -> p));

    // Second pass: assign new indexes based on requested order
    for (int newIndex = 0; newIndex < requestedIndexes.size(); newIndex++) {
      int oldIndex = requestedIndexes.get(newIndex);
      QuestPostEntity post = indexToPost.get(oldIndex);
      post.setPostIndex(newIndex);
    }
    questPostRepository.saveAll(indexToPost.values());

    log.info("Reordered {} posts in quest {}", requestedIndexes.size(), questId);

    // Return updated quest
    List<QuestPostEntity> reorderedPosts =
        questPostRepository.findByQuestIdOrderByPostIndexAsc(quest.getId());
    List<QuestPostDto> postDtos = reorderedPosts.stream()
        .map(questPostMapper::convertToDto)
        .toList();

    return questMapper.convertToDto(quest, postDtos, countTotalBlocks(quest));
  }

  /**
   * List contributors for a specific quest.
   * Results are cached for 1 minute to reduce database load.
   */
  @Cacheable(
      value = CacheNames.QUEST_CONTRIBUTORS,
      key = "#questId + '-' + #minBattles + '-' + #limit")
  @Transactional(readOnly = true)
  public QuestContributorsDto listQuestContributors(
      String questId, int minBattles, int limit) {
    log.debug("Fetching contributors for quest: {}", questId);

    QuestEntity quest = findQuestByQuestId(questId);

    List<ContributorProjection> contributorProjections =
        battleRepository.findContributorsByDateRange(
            quest.getStartDate(), quest.getEndDate(), minBattles, PageRequest.of(0, limit));

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime effectiveEndDate =
        now.isAfter(quest.getEndDate()) ? quest.getEndDate() : now;
    double questWeeks = calculateWeeks(quest.getStartDate(), effectiveEndDate);

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

  private QuestEntity findQuestByQuestId(String questId) {
    return questRepository
        .findByQuestId(questId)
        .orElseThrow(() -> new QuestNotFoundException("Quest not found: " + questId));
  }

  private Integer countTotalBlocks(QuestEntity quest) {
    Long count = battleRepository.countCompletedByDateRange(
        quest.getStartDate(), quest.getEndDate());
    return count != null ? count.intValue() : 0;
  }

  /**
   * Resolve the caller's tier based on their battle count during the quest period.
   * Returns null for anonymous users.
   */
  private String resolveTier(UUID userId, QuestEntity quest) {
    if (userId == null) {
      return null;
    }

    Long battleCount = battleRepository.countCompletedByUserIdAndDateRange(
        userId, quest.getStartDate(), quest.getEndDate());
    if (battleCount == null || battleCount == 0) {
      return null;
    }

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime effectiveEndDate =
        now.isAfter(quest.getEndDate()) ? quest.getEndDate() : now;
    double questWeeks = calculateWeeks(quest.getStartDate(), effectiveEndDate);
    double battlesPerWeek = battleCount / questWeeks;

    if (battlesPerWeek >= CHAMPION_THRESHOLD) {
      return "champion";
    } else if (battlesPerWeek >= KNIGHT_THRESHOLD) {
      return "knight";
    }
    return null;
  }

  private boolean isPublished(QuestPostEntity post, OffsetDateTime now) {
    return post.getPublishDate() == null || !now.isBefore(post.getPublishDate());
  }

  private boolean isUnlocked(QuestPostEntity post, int totalBlocks, String callerTier) {
    // Check progress gate
    if (post.getRequiredProgress() != null && totalBlocks < post.getRequiredProgress()) {
      return false;
    }

    // Check tier gate
    if (post.getRequiredTier() != null) {
      if (callerTier == null) {
        return false;
      }
      if (!meetsMinimumTier(callerTier, post.getRequiredTier())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Check if callerTier meets the minimum required tier.
   * Hierarchy: champion > knight.
   */
  private boolean meetsMinimumTier(String callerTier, String requiredTier) {
    int callerRank = TIER_HIERARCHY.indexOf(callerTier);
    int requiredRank = TIER_HIERARCHY.indexOf(requiredTier);
    // Lower index = higher tier. -1 means not in the hierarchy at all.
    if (callerRank == -1) {
      return false;
    }
    return callerRank <= requiredRank;
  }

  private double calculateWeeks(OffsetDateTime startDate, OffsetDateTime endDate) {
    Duration duration = Duration.between(startDate, endDate);
    long days = duration.toDays();
    if (days < 1) {
      days = 1;
    }
    return days / 7.0;
  }

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
