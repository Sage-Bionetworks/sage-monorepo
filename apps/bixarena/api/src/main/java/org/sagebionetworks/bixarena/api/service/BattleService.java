package org.sagebionetworks.bixarena.api.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.ModelNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.BattlePageDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.PageMetadataDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.model.mapper.BattleMapper;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.ModelRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BattleService {

  private final BattleRepository battleRepository;
  private final ModelRepository modelRepository;
  private final BattleMapper battleMapper = new BattleMapper();

  @Transactional(readOnly = true)
  public BattlePageDto listBattles(BattleSearchQueryDto query) {
    log.info("List battles with query {}", query);

    BattleSearchQueryDto effectiveQuery = query != null ? query : new BattleSearchQueryDto();

    Pageable pageable = createPageable(effectiveQuery);
    Specification<BattleEntity> spec = buildSpecification(effectiveQuery);

    Page<BattleEntity> page = battleRepository.findAll(spec, pageable);

    return BattlePageDto.builder()
      .battles(battleMapper.convertToDtoList(page.getContent()))
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
  public BattleDto getBattle(UUID battleId) {
    log.info("Get battle with ID: {}", battleId);
    BattleEntity battleEntity = getBattleEntity(battleId);
    return battleMapper.convertToDto(battleEntity);
  }

  @Transactional
  public BattleCreateResponseDto createBattle(
    BattleCreateRequestDto request,
    Authentication authentication
  ) {
    // Extract user ID from JWT token's 'sub' claim
    String sub = authentication.getName();
    UUID userId;
    try {
      userId = UUID.fromString(sub);
    } catch (IllegalArgumentException e) {
      log.error("Invalid UUID format in JWT sub claim: {}", sub);
      throw new IllegalStateException("Invalid user ID in authentication token");
    }

    log.info("Creating battle for user: {}", userId);

    // Randomly select 2 active models
    List<ModelEntity> randomModels = modelRepository.findRandomActiveModels(2);

    if (randomModels.size() < 2) {
      throw new IllegalStateException(
        "Not enough active models available. Found: " + randomModels.size()
      );
    }

    ModelEntity model1 = randomModels.get(0);
    ModelEntity model2 = randomModels.get(1);

    log.info("Randomly selected models: {} vs {}", model1.getName(), model2.getName());

    // Create new battle entity
    BattleEntity battle = BattleEntity.builder()
      .title(request.getTitle())
      .userId(userId)
      .model1Id(model1.getId())
      .model2Id(model2.getId())
      .build();

    // Save the battle
    BattleEntity savedBattle = battleRepository.save(battle);
    battleRepository.flush();

    log.info("Successfully created battle with ID: {}", savedBattle.getId());

    // Convert to response DTO with full model information
    return battleMapper.convertToCreateResponseDto(savedBattle, model1, model2);
  }

  @Transactional
  public BattleDto updateBattle(UUID battleId, BattleUpdateRequestDto request) {
    log.info("Updating battle with ID: {}", battleId);

    BattleEntity existingBattle = getBattleEntity(battleId);
    boolean wasIncomplete = existingBattle.getEndedAt() == null;

    // Update title if provided
    if (request.getTitle() != null) {
      existingBattle.setTitle(request.getTitle());
    }

    // Handle endedAt: use provided value, or auto-set to now if no ended time provided
    if (request.getEndedAt() != null) {
      existingBattle.setEndedAt(request.getEndedAt());
    } else if (existingBattle.getEndedAt() == null) {
      existingBattle.setEndedAt(java.time.OffsetDateTime.now());
      log.info("Auto-setting endedAt for battle {}", battleId);
    }

    // Save the updated battle
    BattleEntity updatedBattle = battleRepository.save(existingBattle);

    // Invalidate user rank cache if battle was just completed
    boolean isNowComplete = updatedBattle.getEndedAt() != null;
    if (wasIncomplete && isNowComplete) {
      invalidateUserRankCache(updatedBattle.getUserId());
    }

    log.info("Successfully updated battle with ID: {}", battleId);

    return battleMapper.convertToDto(updatedBattle);
  }

  /**
   * Invalidate the cached rank for a specific user.
   * Called when a user completes a battle, as their rank may have changed.
   *
   * @param userId The user whose rank cache should be invalidated
   */
  @CacheEvict(value = CacheNames.USER_RANKS, key = "#userId")
  public void invalidateUserRankCache(UUID userId) {
    log.info("Invalidated rank cache for user: {}", userId);
  }

  @Transactional
  public void deleteBattle(UUID battleId) {
    log.info("Deleting battle with ID: {}", battleId);

    // Verify battle exists before deletion
    getBattleEntity(battleId);

    // Delete the battle
    battleRepository.deleteById(battleId);
    log.info("Successfully deleted battle with ID: {}", battleId);
  }

  private BattleEntity getBattleEntity(UUID battleId) {
    return battleRepository
      .findById(battleId)
      .orElseThrow(() ->
        new BattleNotFoundException(
          String.format("The battle with ID %s does not exist.", battleId)
        )
      );
  }

  private ModelEntity getModelEntity(UUID modelId) {
    return modelRepository
      .findById(modelId)
      .orElseThrow(() ->
        new ModelNotFoundException(String.format("The model with ID %s does not exist.", modelId))
      );
  }

  private Pageable createPageable(BattleSearchQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(100),
      sort
    );
  }

  private Sort createSort(BattleSearchQueryDto query) {
    String sortField = Optional.ofNullable(query.getSort()).map(Enum::name).orElse("CREATED_AT");
    String directionStr = Optional.ofNullable(query.getDirection()).map(Enum::name).orElse("DESC");

    Sort.Direction direction = "DESC".equalsIgnoreCase(directionStr)
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    String entityField =
      switch (sortField.toLowerCase()) {
        case "ended_at" -> "endedAt";
        default -> "createdAt"; // CREATED_AT
      };

    return Sort.by(direction, entityField);
  }

  private Specification<BattleEntity> buildSpecification(BattleSearchQueryDto query) {
    return userIdFilter(query);
  }

  private Specification<BattleEntity> userIdFilter(BattleSearchQueryDto query) {
    if (query.getUserId() == null) {
      return null; // no filtering
    }
    UUID userId = query.getUserId();
    return (root, cq, cb) -> cb.equal(root.get("userId"), userId);
  }
}
