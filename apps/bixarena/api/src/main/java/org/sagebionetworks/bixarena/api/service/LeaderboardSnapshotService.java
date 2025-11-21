package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.LeaderboardNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardSnapshotMapper;
import org.sagebionetworks.bixarena.api.model.projection.SnapshotWithEntryCount;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardSnapshotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardSnapshotService {

  private final LeaderboardRepository leaderboardRepository;
  private final LeaderboardSnapshotRepository snapshotRepository;
  private final LeaderboardSnapshotMapper snapshotMapper = new LeaderboardSnapshotMapper();

  @Transactional(readOnly = true)
  public LeaderboardSnapshotPageDto getLeaderboardSnapshots(
    String leaderboardId,
    LeaderboardSnapshotQueryDto query
  ) {
    log.info("Getting snapshots for leaderboard {} with query {}", leaderboardId, query);

    // Use default query if not provided
    LeaderboardSnapshotQueryDto snapshotQuery = query != null
      ? query
      : new LeaderboardSnapshotQueryDto();

    LeaderboardEntity leaderboard = findLeaderboardBySlug(leaderboardId);

    // Create pageable with sorting
    Pageable pageable = createPageable(snapshotQuery);

    // Find public snapshots for this leaderboard with entry counts in a single query
    Page<SnapshotWithEntryCount> snapshotsPage =
      snapshotRepository.findPublicSnapshotsWithEntryCountByLeaderboard(leaderboard, pageable);

    return LeaderboardSnapshotPageDto.builder()
      .snapshots(snapshotMapper.convertFromProjectionList(snapshotsPage.getContent()))
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
