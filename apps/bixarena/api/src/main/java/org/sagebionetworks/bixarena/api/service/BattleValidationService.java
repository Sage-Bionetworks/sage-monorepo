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
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleValidationEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleValidationRepository;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleValidationService {

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
   * and persisting the result.
   *
   * <p>This method is fire-and-forget: errors are logged but do NOT block voting.
   */
  @Async("validationExecutor")
  @Transactional
  public void validateAndPersistBattle(UUID battleId) {
    try {
      // Collect all user prompts from the battle's rounds
      List<BattleRoundEntity> rounds = battleRoundRepository
        .findByBattleIdOrderByRoundNumberAsc(battleId);

      List<String> prompts = rounds.stream()
        .map(round -> messageRepository.findById(round.getPromptMessageId()))
        .filter(java.util.Optional::isPresent)
        .map(opt -> opt.get().getContent())
        .toList();

      if (prompts.isEmpty()) {
        log.warn("No prompts found for battle {} — skipping battle validation", battleId);
        return;
      }

      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiBattleValidationResponse validation = callAiService(serviceToken, prompts);
      persistValidation(battleId, validation);
      log.info(
        "Battle validation persisted for battle {}: method={}, confidence={}, isBiomedical={}",
        battleId,
        validation.method(),
        validation.confidence(),
        validation.isBiomedical()
      );
    } catch (Exception e) {
      log.warn("Battle validation failed for battle {} — skipping: {}", battleId, e.getMessage());
    }
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

  private void persistValidation(UUID battleId, AiBattleValidationResponse validation) {
    BattleValidationEntity entity = BattleValidationEntity.builder()
      .battleId(battleId)
      .method(validation.method())
      .confidence(validation.confidence())
      .isBiomedical(validation.isBiomedical())
      .build();

    battleValidationRepository.save(entity);
    battleValidationRepository.flush();
  }
}
