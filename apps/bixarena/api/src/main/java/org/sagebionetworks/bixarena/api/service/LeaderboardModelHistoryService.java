package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.LeaderboardModelNotFoundException;
import org.sagebionetworks.bixarena.api.exception.LeaderboardNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardModelHistoryMapper;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardEntryRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardRepository;
import org.sagebionetworks.bixarena.api.model.repository.ModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LeaderboardModelHistoryService {

  private final LeaderboardRepository leaderboardRepository;
  private final ModelRepository modelRepository;
  private final LeaderboardEntryRepository entryRepository;
  private final LeaderboardModelHistoryMapper historyMapper = new LeaderboardModelHistoryMapper();

  public LeaderboardModelHistoryService(
    LeaderboardRepository leaderboardRepository,
    ModelRepository modelRepository,
    LeaderboardEntryRepository entryRepository
  ) {
    this.leaderboardRepository = leaderboardRepository;
    this.modelRepository = modelRepository;
    this.entryRepository = entryRepository;
  }

  @Transactional(readOnly = true)
  public LeaderboardModelHistoryPageDto getModelHistory(
    String leaderboardId,
    String modelId,
    LeaderboardModelHistoryQueryDto query
  ) {
    log.info(
      "Getting model history for leaderboard {} and model {} with query {}",
      leaderboardId,
      modelId,
      query
    );

    // Use default query if not provided
    LeaderboardModelHistoryQueryDto historyQuery = query != null
      ? query
      : new LeaderboardModelHistoryQueryDto();

    LeaderboardEntity leaderboard = findLeaderboardBySlug(leaderboardId);
    ModelEntity model = findModelBySlug(modelId);

    // Create pageable with sorting
    Pageable pageable = createPageable(historyQuery);

    // Find historical entries for this model in this leaderboard
    Page<LeaderboardEntryEntity> entriesPage = entryRepository.findByLeaderboardAndModel(
      leaderboard,
      model,
      pageable
    );

    return LeaderboardModelHistoryPageDto.builder()
      .modelId(model.getSlug())
      .modelName(model.getName())
      .history(historyMapper.convertToDtoList(entriesPage.getContent()))
      .number(entriesPage.getNumber())
      .size(entriesPage.getSize())
      .totalElements(entriesPage.getTotalElements())
      .totalPages(entriesPage.getTotalPages())
      .hasNext(entriesPage.hasNext())
      .hasPrevious(entriesPage.hasPrevious())
      .build();
  }

  private LeaderboardEntity findLeaderboardBySlug(String slug) {
    return leaderboardRepository
      .findBySlug(slug)
      .orElseThrow(() -> new LeaderboardNotFoundException("Leaderboard not found: " + slug));
  }

  private ModelEntity findModelBySlug(String slug) {
    return modelRepository
      .findBySlug(slug)
      .orElseThrow(() -> new LeaderboardModelNotFoundException("Model not found: " + slug));
  }

  private Pageable createPageable(LeaderboardModelHistoryQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(100),
      sort
    );
  }

  private Sort createSort(LeaderboardModelHistoryQueryDto query) {
    String sortField = Optional.ofNullable(query.getSort())
      .map(s -> s.getValue())
      .orElse("created_at");

    Sort.Direction direction = Optional.ofNullable(query.getDirection())
      .map(d -> "desc".equalsIgnoreCase(d.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC)
      .orElse(Sort.Direction.DESC); // Default to DESC for history (newest first)

    // Map API sort fields to entity fields
    String entityField =
      switch (sortField) {
        case "bt_score" -> "btScore";
        case "rank" -> "rank";
        default -> "createdAt";
      };

    return Sort.by(direction, entityField);
  }
}
