package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.ModelNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
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
  public BattleDto createBattle(BattleCreateRequestDto request, Authentication authentication) {
    // MOCK: Hardcoded userId for development
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    log.info("Creating battle for MOCK user: {}", userId);

    // Validate that both models exist
    UUID leftModelId = request.getLeftModelId();
    UUID rightModelId = request.getRightModelId();

    // Verify models exist (throws exception if not found)
    getModelEntity(leftModelId);
    getModelEntity(rightModelId);

    // Create new battle entity
    BattleEntity battle = BattleEntity.builder()
      .title(request.getTitle())
      .userId(userId)
      .leftModelId(leftModelId)
      .rightModelId(rightModelId)
      .build();

    // Save the battle
    BattleEntity savedBattle = battleRepository.save(battle);
    battleRepository.flush();

    log.info("Successfully created battle with ID: {}", savedBattle.getId());

    return battleMapper.convertToDto(savedBattle);
  }

  @Transactional
  public BattleDto updateBattle(UUID battleId, BattleUpdateRequestDto request) {
    log.info("Updating battle with ID: {}", battleId);

    BattleEntity existingBattle = getBattleEntity(battleId);

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
    log.info("Successfully updated battle with ID: {}", battleId);

    return battleMapper.convertToDto(updatedBattle);
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
