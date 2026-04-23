package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.sagebionetworks.bixarena.api.exception.ExamplePromptNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.sagebionetworks.bixarena.api.model.dto.CategorizationStatusDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationResponseDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptCategorizationRepository;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamplePromptCategorizationService {

  private final ExamplePromptRepository examplePromptRepository;
  private final ExamplePromptCategorizationRepository categorizationRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** Response from the AI service /categorize-prompt endpoint. */
  private record AiCategorizationResponse(String prompt, String category, String method) {}

  /**
   * Calls the AI service to categorize a prompt and persists the result.
   * Fire-and-forget: errors are logged but do NOT block prompt creation.
   */
  @Async("validationExecutor")
  @Transactional
  public void categorizePromptAsync(UUID promptId) {
    try {
      categorizePrompt(promptId);
    } catch (Exception e) {
      log.warn(
        "Prompt categorization failed for prompt {} — skipping: {}",
        promptId,
        e.getMessage()
      );
    }
  }

  /**
   * Calls the AI service to categorize a prompt and always persists a row —
   * including when the classifier abstained (status='abstained') or the AI
   * service itself errored (status='failed'). Persisting every attempt
   * preserves the audit trail and prevents redundant LLM calls.
   *
   * <p>Only rows with status='matched' auto-promote to the effective
   * categorization via compare-and-set. Subsequent matched AI re-runs are
   * persisted but do not override the effective categorization.
   */
  @Transactional
  public ExamplePromptCategorizationResponseDto categorizePrompt(UUID promptId) {
    ExamplePromptEntity prompt = getPromptOrThrow(promptId);

    String method;
    String category = null;
    String status;

    try {
      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiCategorizationResponse aiResponse = callAiService(serviceToken, prompt.getQuestion());
      method = aiResponse.method();
      category = aiResponse.category();
      status = category != null
        ? CategorizationStatusDto.MATCHED.getValue()
        : CategorizationStatusDto.ABSTAINED.getValue();
    } catch (Exception e) {
      // AI service unreachable or returned non-200. Persist a failed row so
      // admins can identify and retry; do not let transient errors crash the
      // caller (e.g. the async trigger after a user vote).
      log.warn(
        "AI categorization failed for prompt {} — persisting status=failed: {}",
        promptId,
        e.getMessage()
      );
      method = "ai-service-error";
      status = CategorizationStatusDto.FAILED.getValue();
    }

    ExamplePromptCategorizationEntity entity = persistCategorization(
      promptId,
      method,
      category,
      null,
      null,
      status
    );

    // Only matched rows auto-promote to effective. Abstained and failed rows
    // are persisted for audit but never claim the effective pointer.
    if (CategorizationStatusDto.MATCHED.getValue().equals(status)) {
      int updated = examplePromptRepository.setEffectiveCategorizationIfNull(
        promptId,
        entity.getId()
      );
      if (updated > 0) {
        log.info(
          "Auto-set effective categorization {} for prompt {}",
          entity.getId(),
          promptId
        );
      }
    }

    log.info(
      "Prompt categorization persisted for prompt {}: status={}, method={}, category={}",
      promptId,
      status,
      method,
      category
    );
    return toDto(entity);
  }

  /**
   * Creates a human override categorization. Always becomes the effective categorization.
   */
  @Transactional
  public ExamplePromptCategorizationResponseDto createManualCategorization(
    UUID promptId,
    ExamplePromptCategorizationCreateRequestDto request,
    UUID categorizedBy
  ) {
    ExamplePromptEntity prompt = getPromptOrThrow(promptId);

    String categorySlug = request.getCategory().getValue();

    ExamplePromptCategorizationEntity entity = persistCategorization(
      promptId,
      "human-review",
      categorySlug,
      categorizedBy,
      request.getReason(),
      CategorizationStatusDto.MATCHED.getValue()
    );

    prompt.setEffectiveCategorizationId(entity.getId());
    examplePromptRepository.save(prompt);

    log.info(
      "Human categorization {} set as effective for prompt {}",
      entity.getId(),
      promptId
    );
    return toDto(entity);
  }

  /**
   * Lists all categorizations for a prompt, most recent first.
   */
  @Transactional(readOnly = true)
  public List<ExamplePromptCategorizationResponseDto> listCategorizations(UUID promptId) {
    if (!examplePromptRepository.existsById(promptId)) {
      throw new ExamplePromptNotFoundException("Example prompt not found: " + promptId);
    }
    return categorizationRepository
      .findByPromptIdOrderByCreatedAtDesc(promptId)
      .stream()
      .map(this::toDto)
      .toList();
  }

  public ExamplePromptCategorizationResponseDto toDto(ExamplePromptCategorizationEntity entity) {
    ExamplePromptCategorizationResponseDto dto = new ExamplePromptCategorizationResponseDto();
    dto.setId(entity.getId());
    dto.setPromptId(entity.getPromptId());
    dto.setStatus(CategorizationStatusDto.fromValue(entity.getStatus()));
    // category is non-null only for status='matched'.
    dto.setCategory(
      entity.getCategory() == null ? null : BiomedicalCategoryDto.fromValue(entity.getCategory())
    );
    dto.setMethod(entity.getMethod());
    dto.setCategorizedBy(entity.getCategorizedBy());
    dto.setReason(entity.getReason());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }

  private ExamplePromptCategorizationEntity persistCategorization(
    UUID promptId,
    String method,
    String categorySlug,
    UUID categorizedBy,
    String reason,
    String status
  ) {
    ExamplePromptCategorizationEntity entity = ExamplePromptCategorizationEntity.builder()
      .promptId(promptId)
      .method(method)
      .categorizedBy(categorizedBy)
      .reason(reason)
      .status(status)
      .category(categorySlug)
      .build();

    categorizationRepository.save(entity);
    categorizationRepository.flush();
    return entity;
  }

  private AiCategorizationResponse callAiService(String token, String promptText) throws Exception {
    String jsonBody = objectMapper.writeValueAsString(Map.of("prompt", promptText));
    String url = appProperties.aiService().baseUrl() + "/categorize-prompt";

    log.debug("Calling AI service at {}", url);

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

  private ExamplePromptEntity getPromptOrThrow(UUID promptId) {
    return examplePromptRepository
      .findById(promptId)
      .orElseThrow(() ->
        new ExamplePromptNotFoundException("Example prompt not found: " + promptId)
      );
  }
}
