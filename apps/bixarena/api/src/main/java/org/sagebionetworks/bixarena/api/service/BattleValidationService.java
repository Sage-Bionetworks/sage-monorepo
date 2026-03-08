package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.DuplicateBattleValidationException;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationResponseDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleValidationEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleValidationRepository;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleValidationService {

  private final BattleRepository battleRepository;
  private final BattleValidationRepository battleValidationRepository;
  private final BattleRoundRepository battleRoundRepository;
  private final MessageRepository messageRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** Response from the AI service /validate-battle endpoint. */
  private record AiBattleValidationResponse(
    BigDecimal confidence,
    @JsonProperty("isBiomedical") boolean isBiomedical,
    String method
  ) {}

  /**
   * Validates all user prompts in a battle by calling the AI service
   * and persisting the result. Fire-and-forget: errors are logged but do NOT block voting.
   */
  @Async("validationExecutor")
  @Transactional
  public void validateAndPersistBattleAsync(UUID battleId) {
    try {
      validateAndPersistBattle(battleId);
    } catch (Exception e) {
      log.warn(
        "Battle validation failed for battle {} — skipping: {}",
        battleId, e.getMessage()
      );
    }
  }

  /**
   * Validates all user prompts in a battle by calling the AI service
   * and persisting the result. Returns the persisted entity.
   *
   * @throws IllegalStateException if no prompts found or AI service call fails
   */
  @Transactional
  public BattleValidationEntity validateAndPersistBattle(UUID battleId) {
    List<String> prompts = collectPrompts(battleId);

    if (prompts.isEmpty()) {
      throw new IllegalStateException(
        "No prompts found for battle " + battleId
      );
    }

    try {
      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiBattleValidationResponse validation = callAiService(serviceToken, prompts);
      BattleValidationEntity entity = persistValidation(battleId, validation);
      log.info(
        "Battle validation persisted for battle {}: method={}, confidence={}, isBiomedical={}",
        battleId,
        validation.method(),
        validation.confidence(),
        validation.isBiomedical()
      );
      return entity;
    } catch (Exception e) {
      throw new IllegalStateException(
        "Battle validation failed for battle " + battleId + ": " + e.getMessage(), e
      );
    }
  }

  /**
   * Creates a human review validation for a battle.
   */
  @Transactional
  public BattleValidationResponseDto createHumanValidation(
      UUID battleId, BattleValidationCreateRequestDto request, UUID validatorId) {
    if (!battleRepository.existsById(battleId)) {
      throw new BattleNotFoundException("Battle not found: " + battleId);
    }

    BattleValidationEntity entity = BattleValidationEntity.builder()
      .battleId(battleId)
      .method("human-review")
      .confidence(request.getIsBiomedical() ? BigDecimal.ONE : BigDecimal.ZERO)
      .isBiomedical(request.getIsBiomedical())
      .validatedBy(validatorId)
      .reason(request.getReason())
      .build();

    try {
      BattleValidationEntity saved = battleValidationRepository.save(entity);
      battleValidationRepository.flush();
      return toDto(saved);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateBattleValidationException(battleId, "human-review");
    }
  }

  /**
   * Lists all validations for a battle, most recent first.
   */
  public List<BattleValidationResponseDto> listValidations(UUID battleId) {
    return battleValidationRepository
      .findByBattleIdOrderByCreatedAtDesc(battleId)
      .stream()
      .map(this::toDto)
      .toList();
  }

  /** Maps a validation entity to its response DTO. */
  public BattleValidationResponseDto toDto(BattleValidationEntity entity) {
    BattleValidationResponseDto dto = new BattleValidationResponseDto();
    dto.setId(entity.getId());
    dto.setBattleId(entity.getBattleId());
    dto.setMethod(entity.getMethod());
    dto.setConfidence(entity.getConfidence().floatValue());
    dto.setIsBiomedical(entity.getIsBiomedical());
    dto.setValidatedBy(entity.getValidatedBy());
    dto.setReason(entity.getReason());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }

  private List<String> collectPrompts(UUID battleId) {
    List<BattleRoundEntity> rounds = battleRoundRepository
      .findByBattleIdOrderByRoundNumberAsc(battleId);

    return rounds.stream()
      .map(round -> messageRepository.findById(round.getPromptMessageId()))
      .filter(java.util.Optional::isPresent)
      .map(opt -> opt.get().getContent())
      .toList();
  }

  private AiBattleValidationResponse callAiService(String token, List<String> prompts)
      throws Exception {
    String jsonBody = objectMapper.writeValueAsString(Map.of("prompts", prompts));
    String url = appProperties.aiService().baseUrl() + "/validate-battle";

    log.debug("Calling AI service at {} with {} prompts", url, prompts.size());

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header("Authorization", "Bearer " + token)
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
      .build();

    HttpResponse<String> httpResponse = serviceTokenProvider.getHttpClient().send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (httpResponse.statusCode() != 200) {
      throw new IllegalStateException(
        "AI service returned " + httpResponse.statusCode() + ": " + httpResponse.body()
      );
    }

    return objectMapper.readValue(httpResponse.body(), AiBattleValidationResponse.class);
  }

  private BattleValidationEntity persistValidation(
      UUID battleId, AiBattleValidationResponse validation) {
    BattleValidationEntity entity = BattleValidationEntity.builder()
      .battleId(battleId)
      .method(validation.method())
      .confidence(validation.confidence())
      .isBiomedical(validation.isBiomedical())
      .build();

    battleValidationRepository.save(entity);
    battleValidationRepository.flush();

    // Atomically set as effective only if no effective validation exists yet.
    // This is a compare-and-set at the database level, safe under concurrency.
    int updated = battleRepository.setEffectiveValidationIfNull(battleId, entity.getId());
    if (updated > 0) {
      log.info("Auto-set effective validation {} for battle {}", entity.getId(), battleId);
    }

    return entity;
  }
}
