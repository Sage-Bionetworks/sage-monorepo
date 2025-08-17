package org.sagebionetworks.bixarena.api.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.exception.LeaderboardNotFoundException;
import org.sagebionetworks.bixarena.api.exception.LeaderboardSnapshotNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.ListLeaderboards200ResponseInnerDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardEntryMapper;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardEntryRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaderboardService {

  private static final Logger logger = LoggerFactory.getLogger(LeaderboardService.class);

  private final LeaderboardRepository leaderboardRepository;
  private final LeaderboardSnapshotRepository snapshotRepository;
  private final LeaderboardEntryRepository entryRepository;
  private final LeaderboardEntryMapper entryMapper = new LeaderboardEntryMapper();

  public LeaderboardService(
    LeaderboardRepository leaderboardRepository,
    LeaderboardSnapshotRepository snapshotRepository,
    LeaderboardEntryRepository entryRepository
  ) {
    this.leaderboardRepository = leaderboardRepository;
    this.snapshotRepository = snapshotRepository;
    this.entryRepository = entryRepository;
  }

  @Transactional(readOnly = true)
  public List<ListLeaderboards200ResponseInnerDto> listLeaderboards() {
    logger.info("Listing all leaderboards");

    List<LeaderboardEntity> leaderboards = leaderboardRepository.findAll();

    return leaderboards.stream().map(this::convertToListResponse).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public LeaderboardPageDto getLeaderboard(String leaderboardId, LeaderboardSearchQueryDto query) {
    logger.info("Getting leaderboard {} with query {}", leaderboardId, query);

    // Use default query if not provided
    LeaderboardSearchQueryDto searchQuery = query != null ? query : new LeaderboardSearchQueryDto();

    LeaderboardEntity leaderboard = findLeaderboardBySlug(leaderboardId);

    // Get the specified snapshot or latest if not specified
    LeaderboardSnapshotEntity snapshot = getTargetSnapshot(
      leaderboard,
      searchQuery.getSnapshotId()
    );

    // Create pageable with sorting
    Pageable pageable = createPageable(searchQuery);

    // Find entries with optional search
    Page<LeaderboardEntryEntity> entriesPage = findEntries(
      leaderboard,
      snapshot,
      searchQuery.getSearch(),
      pageable
    );

    return LeaderboardPageDto.builder()
      .entries(entryMapper.convertToDtoList(entriesPage.getContent()))
      .updatedAt(snapshot.getCreatedAt())
      .snapshotId(snapshot.getSnapshotIdentifier())
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

  private LeaderboardSnapshotEntity getTargetSnapshot(
    LeaderboardEntity leaderboard,
    String snapshotId
  ) {
    if (snapshotId != null && !snapshotId.trim().isEmpty()) {
      // Find specific snapshot by identifier
      return snapshotRepository
        .findByLeaderboardOrderByCreatedAtDesc(leaderboard, Pageable.unpaged())
        .getContent()
        .stream()
        .filter(s -> s.getSnapshotIdentifier().equals(snapshotId))
        .findFirst()
        .orElseThrow(() ->
          new LeaderboardSnapshotNotFoundException("Snapshot not found: " + snapshotId)
        );
    } else {
      // Get latest snapshot
      List<LeaderboardSnapshotEntity> snapshots = snapshotRepository.findLatestByLeaderboard(
        leaderboard
      );
      if (snapshots.isEmpty()) {
        throw new LeaderboardSnapshotNotFoundException(
          "No snapshots found for leaderboard: " + leaderboard.getSlug()
        );
      }
      return snapshots.get(0);
    }
  }

  private Pageable createPageable(LeaderboardSearchQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(100),
      sort
    );
  }

  private Sort createSort(LeaderboardSearchQueryDto query) {
    String sortField = Optional.ofNullable(query.getSort()).map(s -> s.getValue()).orElse("rank");

    Sort.Direction direction = Optional.ofNullable(query.getDirection())
      .map(d -> "desc".equalsIgnoreCase(d.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC)
      .orElse(Sort.Direction.ASC);

    // Map API sort fields to entity fields
    String entityField =
      switch (sortField) {
        case "bt_score" -> "btScore";
        case "vote_count" -> "voteCount";
        case "created_at" -> "createdAt";
        case "model_name" -> "model.name";
        default -> "rank";
      };

    return Sort.by(direction, entityField);
  }

  private Page<LeaderboardEntryEntity> findEntries(
    LeaderboardEntity leaderboard,
    LeaderboardSnapshotEntity snapshot,
    String search,
    Pageable pageable
  ) {
    if (search != null && !search.trim().isEmpty()) {
      return entryRepository.findByLeaderboardAndSnapshotAndModelNameContaining(
        leaderboard,
        snapshot,
        search.trim(),
        pageable
      );
    } else {
      return entryRepository.findByLeaderboardAndSnapshot(leaderboard, snapshot, pageable);
    }
  }

  private ListLeaderboards200ResponseInnerDto convertToListResponse(LeaderboardEntity entity) {
    // Get latest snapshot for last updated time
    List<LeaderboardSnapshotEntity> snapshots = snapshotRepository.findLatestByLeaderboard(entity);
    OffsetDateTime updatedAt = snapshots.isEmpty()
      ? entity.getUpdatedAt()
      : snapshots.get(0).getCreatedAt();

    return ListLeaderboards200ResponseInnerDto.builder()
      .id(entity.getSlug())
      .name(entity.getName())
      .description(entity.getDescription())
      .updatedAt(updatedAt)
      .build();
  }
}
