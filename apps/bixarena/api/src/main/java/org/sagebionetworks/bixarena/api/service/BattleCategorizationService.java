package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.exception.BattleCategorizationNotFoundException;
import org.sagebionetworks.bixarena.api.exception.BattleNotEligibleForCategorizationException;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BattleCategorizationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCategorizationResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleCategorizationCategoryEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleCategorizationEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleValidationEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleCategorizationCategoryRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleCategorizationRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleValidationRepository;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleCategorizationService {

  private final BattleRepository battleRepository;
  private final BattleCategorizationRepository categorizationRepository;
  private final BattleCategorizationCategoryRepository categoryRepository;
  private final BattleValidationRepository battleValidationRepository;
  private final BattleRoundRepository battleRoundRepository;
  private final MessageRepository messageRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** Response from the AI service /categorize-battle endpoint. */
  private record AiCategorizationResponse(List<String> categories, String method) {}

  /**
   * Calls the AI service to categorize a battle and persists the result.
   * Fire-and-forget: errors are logged but do NOT block downstream flow.
   */
  @Async("validationExecutor")
  @Transactional
  public void categorizeBattleAsync(UUID battleId) {
    try {
      categorizeBattle(battleId);
    } catch (Exception e) {
      log.warn(
        "Battle categorization failed for battle {} — skipping: {}",
        battleId,
        e.getMessage()
      );
    }
  }

  /**
   * Calls the AI service to categorize a battle, persists the result, and returns the DTO.
   * Used for synchronous admin-triggered runs.
   *
   * <p>Only the first AI categorization auto-becomes the effective categorization (compare-and-set).
   * Subsequent AI re-runs are persisted but do not override the effective categorization.
   *
   * @throws BattleNotEligibleForCategorizationException if the battle has no effective validation
   *     or is not biomedical
   */
  @Transactional
  public BattleCategorizationResponseDto categorizeBattle(UUID battleId) {
    assertBiomedicalOrThrow(battleId);
    List<String> prompts = collectPrompts(battleId);

    if (prompts.isEmpty()) {
      throw new IllegalStateException("No prompts found for battle " + battleId);
    }

    try {
      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiCategorizationResponse aiResponse = callAiService(serviceToken, prompts);
      BattleCategorizationEntity entity = persistCategorization(
        battleId,
        aiResponse.method(),
        aiResponse.categories(),
        null,
        null
      );

      int updated = battleRepository.setEffectiveCategorizationIfNull(battleId, entity.getId());
      if (updated > 0) {
        log.info(
          "Auto-set effective categorization {} for battle {}",
          entity.getId(),
          battleId
        );
      }

      log.info(
        "Battle categorization persisted for battle {}: method={}, categories={}",
        battleId,
        aiResponse.method(),
        aiResponse.categories()
      );
      return toDto(entity);
    } catch (BattleNotEligibleForCategorizationException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(
        "Battle categorization failed for battle " + battleId + ": " + e.getMessage(),
        e
      );
    }
  }

  /**
   * Creates a human override categorization. Always becomes the effective categorization.
   *
   * @throws BattleNotEligibleForCategorizationException if the battle is not biomedical —
   *     admins must first override validation to biomedical before categorizing.
   */
  @Transactional
  public BattleCategorizationResponseDto createManualCategorization(
    UUID battleId,
    BattleCategorizationCreateRequestDto request,
    UUID categorizedBy
  ) {
    assertBiomedicalOrThrow(battleId);

    List<String> categorySlugs = request
      .getCategories()
      .stream()
      .map(BiomedicalCategoryDto::getValue)
      .toList();

    BattleCategorizationEntity entity = persistCategorization(
      battleId,
      "human-review",
      categorySlugs,
      categorizedBy,
      request.getReason()
    );

    BattleEntity battle = battleRepository.findById(battleId).orElseThrow();
    battle.setEffectiveCategorizationId(entity.getId());
    battleRepository.save(battle);

    log.info(
      "Human categorization {} set as effective for battle {}",
      entity.getId(),
      battleId
    );
    return toDto(entity);
  }

  /**
   * Lists all categorizations for a battle, most recent first.
   */
  @Transactional(readOnly = true)
  public List<BattleCategorizationResponseDto> listCategorizations(UUID battleId) {
    if (!battleRepository.existsById(battleId)) {
      throw new BattleNotFoundException("Battle not found: " + battleId);
    }
    return categorizationRepository
      .findByBattleIdOrderByCreatedAtDesc(battleId)
      .stream()
      .map(this::toDto)
      .toList();
  }

  /**
   * Returns the effective categorization for a battle.
   */
  @Transactional(readOnly = true)
  public BattleCategorizationResponseDto getEffectiveCategorization(UUID battleId) {
    BattleEntity battle = battleRepository
      .findById(battleId)
      .orElseThrow(() -> new BattleNotFoundException("Battle not found: " + battleId));
    UUID effectiveId = battle.getEffectiveCategorizationId();
    if (effectiveId == null) {
      throw new BattleCategorizationNotFoundException(
        "No effective categorization found for battle: " + battleId
      );
    }
    BattleCategorizationEntity entity = categorizationRepository
      .findById(effectiveId)
      .orElseThrow(() ->
        new BattleCategorizationNotFoundException(
          "Effective categorization not found: " + effectiveId
        )
      );
    return toDto(entity);
  }

  public BattleCategorizationResponseDto toDto(BattleCategorizationEntity entity) {
    List<BiomedicalCategoryDto> categories = categoryRepository
      .findByCategorizationId(entity.getId())
      .stream()
      .map(c -> BiomedicalCategoryDto.fromValue(c.getCategory()))
      .toList();

    BattleCategorizationResponseDto dto = new BattleCategorizationResponseDto();
    dto.setId(entity.getId());
    dto.setBattleId(entity.getBattleId());
    dto.setCategories(categories);
    dto.setMethod(entity.getMethod());
    dto.setCategorizedBy(entity.getCategorizedBy());
    dto.setReason(entity.getReason());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }

  /**
   * Verifies the battle has a biomedical effective validation.
   * All categorization paths (AI + human override) share this gate — categorization records
   * only exist for biomedical battles.
   */
  private void assertBiomedicalOrThrow(UUID battleId) {
    BattleEntity battle = battleRepository
      .findById(battleId)
      .orElseThrow(() -> new BattleNotFoundException("Battle not found: " + battleId));

    UUID effectiveValidationId = battle.getEffectiveValidationId();
    if (effectiveValidationId == null) {
      throw new BattleNotEligibleForCategorizationException(
        "Battle " + battleId + " has no effective validation — cannot categorize."
      );
    }

    BattleValidationEntity validation = battleValidationRepository
      .findById(effectiveValidationId)
      .orElseThrow(() ->
        new BattleNotEligibleForCategorizationException(
          "Battle " + battleId + " effective validation missing — cannot categorize."
        )
      );

    if (!Boolean.TRUE.equals(validation.getIsBiomedical())) {
      throw new BattleNotEligibleForCategorizationException(
        "Battle " + battleId + " is not biomedical — cannot categorize. " +
          "Override validation to biomedical first."
      );
    }
  }

  private List<String> collectPrompts(UUID battleId) {
    List<BattleRoundEntity> rounds = battleRoundRepository.findByBattleIdOrderByRoundNumberAsc(
      battleId
    );

    return rounds
      .stream()
      .map(round -> messageRepository.findById(round.getPromptMessageId()))
      .filter(Optional::isPresent)
      .map(opt -> opt.get().getContent())
      .toList();
  }

  private BattleCategorizationEntity persistCategorization(
    UUID battleId,
    String method,
    List<String> categorySlugs,
    UUID categorizedBy,
    String reason
  ) {
    BattleCategorizationEntity entity = BattleCategorizationEntity.builder()
      .battleId(battleId)
      .method(method)
      .categorizedBy(categorizedBy)
      .reason(reason)
      .build();

    categorizationRepository.save(entity);
    categorizationRepository.flush();

    List<BattleCategorizationCategoryEntity> categoryEntities = categorySlugs
      .stream()
      .map(slug ->
        BattleCategorizationCategoryEntity.builder()
          .categorizationId(entity.getId())
          .category(slug)
          .build()
      )
      .toList();
    categoryRepository.saveAll(categoryEntities);
    categoryRepository.flush();

    return entity;
  }

  private AiCategorizationResponse callAiService(String token, List<String> prompts)
    throws Exception {
    String jsonBody = objectMapper.writeValueAsString(Map.of("prompts", prompts));
    String url = appProperties.aiService().baseUrl() + "/categorize-battle";

    log.debug("Calling AI service at {} with {} prompts", url, prompts.size());

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header("Authorization", "Bearer " + token)
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
      .build();

    HttpResponse<String> httpResponse = serviceTokenProvider
      .getHttpClient()
      .send(request, HttpResponse.BodyHandlers.ofString());

    if (httpResponse.statusCode() != 200) {
      throw new IllegalStateException(
        "AI service returned " + httpResponse.statusCode() + ": " + httpResponse.body()
      );
    }

    return objectMapper.readValue(httpResponse.body(), AiCategorizationResponse.class);
  }
}
