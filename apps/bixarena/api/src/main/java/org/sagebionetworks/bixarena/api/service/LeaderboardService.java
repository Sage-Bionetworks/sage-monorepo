package org.sagebionetworks.bixarena.api.service;

import jakarta.persistence.criteria.JoinType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.sagebionetworks.bixarena.api.exception.LeaderboardNotFoundException;
import org.sagebionetworks.bixarena.api.exception.LeaderboardSnapshotNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardListInnerDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotDto;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardEntryEntity;
import org.sagebionetworks.bixarena.api.model.entity.LeaderboardSnapshotEntity;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardEntryMapper;
import org.sagebionetworks.bixarena.api.model.mapper.LeaderboardSnapshotMapper;
import org.sagebionetworks.bixarena.api.model.projection.SnapshotWithEntryCount;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardEntryRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardEntryRepository.RankBySlug;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardRepository;
import org.sagebionetworks.bixarena.api.model.repository.LeaderboardSnapshotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class LeaderboardService {

  private final LeaderboardRepository leaderboardRepository;
  private final LeaderboardSnapshotRepository snapshotRepository;
  private final LeaderboardEntryRepository entryRepository;
  private final LeaderboardEntryMapper entryMapper = new LeaderboardEntryMapper();
  private final LeaderboardSnapshotMapper snapshotMapper = new LeaderboardSnapshotMapper();

  @Transactional(readOnly = true)
  @Cacheable(value = CacheNames.LEADERBOARD_LIST)
  public List<LeaderboardListInnerDto> listLeaderboards() {
    log.info("Listing all leaderboards");

    List<LeaderboardEntity> leaderboards = leaderboardRepository.findAll();

    return leaderboards.stream().map(this::convertToListResponse).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable(
    value = CacheNames.LEADERBOARD_ENTRIES,
    key =
      "#leaderboardId + '|' + " +
      "(#query == null ? '' : " +
      "(#query.pageNumber ?: 0) + '-' + " +
      "(#query.pageSize ?: 100) + '-' + " +
      "(#query.sort?.toString() ?: '') + '-' + " +
      "(#query.direction?.toString() ?: '') + '-' + " +
      "(#query.search ?: '') + '-' + " +
      "(#query.license?.toString() ?: '') + '-' + " +
      "(#query.organization ?: '') + '-' + " +
      "(#query.snapshotId ?: '') + '-' + " +
      "(#query.lookback ?: 0))"
  )
  public LeaderboardEntryPageDto getLeaderboard(
    String leaderboardId,
    LeaderboardSearchQueryDto query
  ) {
    log.info("Getting leaderboard {} with query {}", leaderboardId, query);

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

    Page<LeaderboardEntryEntity> entriesPage = findEntries(leaderboard, snapshot, searchQuery, pageable);

    List<LeaderboardEntryDto> entryDtos = entryMapper.convertToDtoList(entriesPage.getContent());
    String priorSnapshotId = decorateRankDeltas(
      leaderboard,
      snapshot,
      searchQuery.getLookback(),
      entryDtos
    );

    int entryCount = (int) entryRepository.countBySnapshot(snapshot);
    int voteCount = (int) entryRepository.sumVoteCountBySnapshot(snapshot);

    return LeaderboardEntryPageDto.builder()
      .entries(entryDtos)
      .updatedAt(snapshot.getCreatedAt())
      .snapshotId(snapshot.getSnapshotIdentifier())
      .priorSnapshotId(priorSnapshotId)
      .entryCount(entryCount)
      .voteCount(voteCount)
      .number(entriesPage.getNumber())
      .size(entriesPage.getSize())
      .totalElements(entriesPage.getTotalElements())
      .totalPages(entriesPage.getTotalPages())
      .hasNext(entriesPage.hasNext())
      .hasPrevious(entriesPage.hasPrevious())
      .build();
  }

  private String decorateRankDeltas(
    LeaderboardEntity leaderboard,
    LeaderboardSnapshotEntity currentSnapshot,
    Integer lookbackDays,
    List<LeaderboardEntryDto> entryDtos
  ) {
    if (lookbackDays == null) {
      return null;
    }
    OffsetDateTime target = currentSnapshot.getCreatedAt().minusDays(lookbackDays);
    List<LeaderboardSnapshotEntity> priorMatches = snapshotRepository.findLatestPublicAtOrBefore(
      leaderboard,
      target,
      currentSnapshot.getId(),
      PageRequest.of(0, 1)
    );
    if (priorMatches.isEmpty()) {
      return null;
    }
    LeaderboardSnapshotEntity prior = priorMatches.get(0);
    Map<String, Integer> priorRanks = entryRepository
      .findRanksBySnapshotId(prior.getId())
      .stream()
      .collect(Collectors.toMap(RankBySlug::getModelSlug, RankBySlug::getRank));
    for (LeaderboardEntryDto dto : entryDtos) {
      Integer priorRank = priorRanks.get(dto.getModelId());
      dto.setRankDelta(priorRank == null ? null : priorRank - dto.getRank());
    }
    return prior.getSnapshotIdentifier();
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
      // Find specific snapshot by identifier - must be public
      LeaderboardSnapshotEntity snapshot = snapshotRepository
        .findByLeaderboardOrderByCreatedAtDesc(leaderboard, Pageable.unpaged())
        .getContent()
        .stream()
        .filter(s -> s.getSnapshotIdentifier().equals(snapshotId))
        .findFirst()
        .orElseThrow(() ->
          new LeaderboardSnapshotNotFoundException("Leaderboard Snapshot not found: " + snapshotId)
        );

      // Check if snapshot is public
      if (!"public".equals(snapshot.getVisibility())) {
        throw new LeaderboardSnapshotNotFoundException(
          "Leaderboard Snapshot not found: " + snapshotId
        );
      }

      return snapshot;
    } else {
      // Get latest public snapshot
      List<LeaderboardSnapshotEntity> snapshots = snapshotRepository.findLatestPublicByLeaderboard(
        leaderboard
      );
      if (snapshots.isEmpty()) {
        throw new LeaderboardSnapshotNotFoundException(
          "No public snapshots found for leaderboard: " + leaderboard.getSlug()
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
        case "model_slug" -> "model.slug";
        case "created_at" -> "createdAt";
        default -> "rank";
      };

    if ("rank".equals(sortField)) {
      // Create primary rank sort, and add secondary btScore sort
      // Since rank is inversely proportional to btScore, use opposite direction
      Sort.Direction btScoreDirection = direction == Sort.Direction.ASC
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      return Sort.by(direction, entityField).and(Sort.by(btScoreDirection, "btScore"));
    }

    return Sort.by(direction, entityField);
  }

  private Page<LeaderboardEntryEntity> findEntries(
    LeaderboardEntity leaderboard,
    LeaderboardSnapshotEntity snapshot,
    LeaderboardSearchQueryDto searchQuery,
    Pageable pageable
  ) {
    Specification<LeaderboardEntryEntity> spec = Specification.where(
      scopeFilter(leaderboard, snapshot)
    )
      .and(searchFilter(searchQuery))
      .and(licenseFilter(searchQuery))
      .and(organizationFilter(searchQuery))
      .and(fetchModel());
    return entryRepository.findAll(spec, pageable);
  }

  private Specification<LeaderboardEntryEntity> scopeFilter(
    LeaderboardEntity leaderboard,
    LeaderboardSnapshotEntity snapshot
  ) {
    return (root, cq, cb) ->
      cb.and(cb.equal(root.get("leaderboard"), leaderboard), cb.equal(root.get("snapshot"), snapshot));
  }

  private Specification<LeaderboardEntryEntity> searchFilter(LeaderboardSearchQueryDto query) {
    if (query.getSearch() == null || query.getSearch().trim().isEmpty()) {
      return null;
    }
    String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("model").get("name")), pattern);
  }

  private Specification<LeaderboardEntryEntity> licenseFilter(LeaderboardSearchQueryDto query) {
    if (query.getLicense() == null) {
      return null;
    }
    String licenseValue = query.getLicense().getValue();
    return (root, cq, cb) -> cb.equal(root.get("model").get("license"), licenseValue);
  }

  private Specification<LeaderboardEntryEntity> organizationFilter(LeaderboardSearchQueryDto query) {
    if (query.getOrganization() == null || query.getOrganization().trim().isEmpty()) {
      return null;
    }
    String value = query.getOrganization().trim().toLowerCase();
    return (root, cq, cb) -> cb.equal(cb.lower(root.get("model").get("organization")), value);
  }

  // Re-apply the JOIN FETCH that the previous JPQL had, but only on the result query
  // (not on the count query Spring Data issues for pagination).
  private Specification<LeaderboardEntryEntity> fetchModel() {
    return (root, cq, cb) -> {
      Class<?> resultType = cq.getResultType();
      if (resultType != Long.class && resultType != long.class) {
        root.fetch("model", JoinType.INNER);
      }
      return null;
    };
  }

  private LeaderboardListInnerDto convertToListResponse(LeaderboardEntity entity) {
    // Latest PUBLIC snapshot only
    Page<SnapshotWithEntryCount> latestPage =
      snapshotRepository.findPublicSnapshotsWithEntryCountByLeaderboard(
        entity,
        PageRequest.of(0, 1)
      );
    LeaderboardSnapshotDto latestSnapshot = latestPage.hasContent()
      ? snapshotMapper.convertFromProjection(latestPage.getContent().get(0))
      : null;

    OffsetDateTime updatedAt = latestSnapshot != null
      ? latestSnapshot.getCreatedAt()
      : entity.getUpdatedAt();

    return LeaderboardListInnerDto.builder()
      .id(entity.getSlug())
      .name(entity.getName())
      .description(entity.getDescription())
      .updatedAt(updatedAt)
      .latestSnapshot(latestSnapshot)
      .build();
  }
}
