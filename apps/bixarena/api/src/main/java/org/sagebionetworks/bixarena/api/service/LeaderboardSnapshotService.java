package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import org.sagebionetworks.bixarena.api.exception.LeaderboardNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardSnapshotMapper;
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
public class LeaderboardSnapshotService {

  private static final Logger logger = LoggerFactory.getLogger(LeaderboardSnapshotService.class);

  private final LeaderboardRepository leaderboardRepository;
  private final LeaderboardSnapshotRepository snapshotRepository;
  private final LeaderboardSnapshotMapper snapshotMapper = new LeaderboardSnapshotMapper();

  public LeaderboardSnapshotService(
    LeaderboardRepository leaderboardRepository,
    LeaderboardSnapshotRepository snapshotRepository
  ) {
    this.leaderboardRepository = leaderboardRepository;
    this.snapshotRepository = snapshotRepository;
  }

  @Transactional(readOnly = true)
  public LeaderboardSnapshotPageDto getLeaderboardSnapshots(
    String leaderboardId,
    LeaderboardSnapshotQueryDto query
  ) {
    logger.info("Getting snapshots for leaderboard {} with query {}", leaderboardId, query);

    // Use default query if not provided
    LeaderboardSnapshotQueryDto snapshotQuery = query != null
      ? query
      : new LeaderboardSnapshotQueryDto();

    LeaderboardEntity leaderboard = findLeaderboardBySlug(leaderboardId);

    // Create pageable with sorting
    Pageable pageable = createPageable(snapshotQuery);

    // Find snapshots for this leaderboard
    Page<LeaderboardSnapshotEntity> snapshotsPage =
      snapshotRepository.findByLeaderboardOrderByCreatedAtDesc(leaderboard, pageable);

    return LeaderboardSnapshotPageDto.builder()
      .snapshots(snapshotMapper.convertToDtoList(snapshotsPage.getContent()))
      .number(snapshotsPage.getNumber())
      .size(snapshotsPage.getSize())
      .totalElements(snapshotsPage.getTotalElements())
      .totalPages(snapshotsPage.getTotalPages())
      .hasNext(snapshotsPage.hasNext())
      .hasPrevious(snapshotsPage.hasPrevious())
      .build();
  }

  private LeaderboardEntity findLeaderboardBySlug(String slug) {
    return leaderboardRepository
      .findBySlug(slug)
      .orElseThrow(() -> new LeaderboardNotFoundException("Leaderboard not found: " + slug));
  }

  private Pageable createPageable(LeaderboardSnapshotQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(100),
      sort
    );
  }

  private Sort createSort(LeaderboardSnapshotQueryDto query) {
    Sort.Direction direction = Optional.ofNullable(query.getDirection())
      .map(d -> "desc".equalsIgnoreCase(d.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC)
      .orElse(Sort.Direction.DESC); // Default to DESC for snapshots (newest first)

    return Sort.by(direction, "createdAt");
  }
}
