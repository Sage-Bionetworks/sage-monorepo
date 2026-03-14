package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.model.entity.PromptValidationEntity;
import org.sagebionetworks.bixarena.api.model.repository.PromptValidationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptValidationService {

  private final PromptValidationRepository promptValidationRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** Response from the AI service /validate-prompt endpoint. */
  private record AiValidationResponse(
    String prompt,
    BigDecimal confidence,
    @JsonProperty("isBiomedical") boolean isBiomedical,
    String method
  ) {}

  /**
   * Validates the prompt for a battle's first round by calling the AI service
   * and persisting the result.
   *
   * <p>This method is fire-and-forget from the caller's perspective: if validation
   * fails for any reason (network error, auth failure, etc.), the error is logged
   * but does NOT block round creation.
   */
  @Async("validationExecutor")
  @Transactional
  public void validateAndPersist(UUID battleId, UUID messageId, String promptText) {
    try {
      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiValidationResponse validation = callAiService(serviceToken, promptText);
      persistValidation(battleId, messageId, validation);
      log.info(
        "Prompt validation persisted for battle {}: method={}, confidence={}, isBiomedical={}",
        battleId,
        validation.method(),
        validation.confidence(),
        validation.isBiomedical()
      );
    } catch (Exception e) {
      log.warn("Prompt validation failed for battle {} — skipping: {}", battleId, e.getMessage());
    }
  }

  private AiValidationResponse callAiService(String token, String promptText) throws Exception {
    String jsonBody = objectMapper.writeValueAsString(Map.of("prompt", promptText));
    String url = appProperties.aiService().baseUrl() + "/validate-prompt";

    log.debug("Calling AI service at {}", url);

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

    return objectMapper.readValue(httpResponse.body(), AiValidationResponse.class);
  }

  private void persistValidation(
    UUID battleId,
    UUID messageId,
    AiValidationResponse validation
  ) {
    PromptValidationEntity entity = PromptValidationEntity.builder()
      .battleId(battleId)
      .messageId(messageId)
      .method(validation.method())
      .confidence(validation.confidence())
      .isBiomedical(validation.isBiomedical())
      .build();

    promptValidationRepository.save(entity);
    promptValidationRepository.flush();
  }
}
