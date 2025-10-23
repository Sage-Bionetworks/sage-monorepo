package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.DuplicateVoteException;
import org.sagebionetworks.bixarena.api.exception.VoteNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.PageMetadataDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteDto;
import org.sagebionetworks.bixarena.api.model.dto.VotePageDto;
import org.sagebionetworks.bixarena.api.model.dto.VotePreferenceDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.VoteEntity;
import org.sagebionetworks.bixarena.api.model.mapper.VoteMapper;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.VoteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class VoteService {

  private final VoteRepository voteRepository;
  private final BattleRepository battleRepository;
  private final VoteMapper voteMapper = new VoteMapper();

  @Transactional(readOnly = true)
  public VotePageDto listVotes(VoteSearchQueryDto query) {
    log.info("List votes with query {}", query);

    VoteSearchQueryDto effectiveQuery = query != null ? query : new VoteSearchQueryDto();

    Pageable pageable = createPageable(effectiveQuery);
    Specification<VoteEntity> spec = buildSpecification(effectiveQuery);

    Page<VoteEntity> page = voteRepository.findAll(spec, pageable);

    return VotePageDto.builder()
      .data(voteMapper.convertToDtoList(page.getContent()))
      .page(
        PageMetadataDto.builder()
          .number(page.getNumber())
          .size(page.getSize())
          .totalElements(page.getTotalElements())
          .totalPages(page.getTotalPages())
          .hasNext(page.hasNext())
          .hasPrevious(page.hasPrevious())
          .build()
      )
      .build();
  }

  @Transactional(readOnly = true)
  public VoteDto getVote(UUID voteId) {
    log.info("Get vote with ID: {}", voteId);
    VoteEntity voteEntity = getVoteEntity(voteId);
    return voteMapper.convertToDto(voteEntity);
  }

  @Transactional
  public VoteDto createVote(VoteCreateRequestDto request) {
    UUID battleId = request.getBattleId();

    log.info("Creating vote for battle ID: {}", battleId);

    // Validate that the battle exists
    getBattleEntity(battleId);

    // Convert DTO preference to entity preference
    VoteEntity.VotePreference entityPreference = convertDtoToEntityPreference(
      request.getPreference()
    );

    // Create new vote entity
    VoteEntity vote = VoteEntity.builder().battleId(battleId).preference(entityPreference).build();

    try {
      // Save the vote
      VoteEntity savedVote = voteRepository.save(vote);
      voteRepository.flush();

      log.info("Successfully created vote with ID: {}", savedVote.getId());

      return voteMapper.convertToDto(savedVote);
    } catch (DataIntegrityViolationException e) {
      // Check if this is the unique constraint violation
      if (e.getMessage() != null && e.getMessage().contains("unique_battle_vote")) {
        throw new DuplicateVoteException(battleId);
      }
      throw e;
    }
  }

  private VoteEntity getVoteEntity(UUID voteId) {
    return voteRepository.findById(voteId).orElseThrow(() -> new VoteNotFoundException(voteId));
  }

  private BattleEntity getBattleEntity(UUID battleId) {
    return battleRepository
      .findById(battleId)
      .orElseThrow(() ->
        new BattleNotFoundException(String.format("Battle not found with ID: %s", battleId))
      );
  }

  private Pageable createPageable(VoteSearchQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(100),
      sort
    );
  }

  private Sort createSort(VoteSearchQueryDto query) {
    String directionStr = Optional.ofNullable(query.getDirection()).map(Enum::name).orElse("DESC");

    Sort.Direction direction = "DESC".equalsIgnoreCase(directionStr)
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    String entityField = "createdAt"; // Only CREATED_AT is supported

    return Sort.by(direction, entityField);
  }

  private Specification<VoteEntity> buildSpecification(VoteSearchQueryDto query) {
    return preferenceFilter(query);
  }

  private Specification<VoteEntity> preferenceFilter(VoteSearchQueryDto query) {
    if (query.getPreference() == null) {
      return null; // no filtering
    }

    VoteEntity.VotePreference entityPreference = convertDtoToEntityPreference(
      query.getPreference()
    );
    return (root, cq, cb) -> cb.equal(root.get("preference"), entityPreference);
  }

  private VoteEntity.VotePreference convertDtoToEntityPreference(VotePreferenceDto dto) {
    if (dto == null) {
      return null;
    }
    return switch (dto) {
      case LEFT_MODEL -> VoteEntity.VotePreference.LEFT_MODEL;
      case RIGHT_MODEL -> VoteEntity.VotePreference.RIGHT_MODEL;
      case TIE -> VoteEntity.VotePreference.TIE;
    };
  }
}
