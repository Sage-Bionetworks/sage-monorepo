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
import org.sagebionetworks.bixarena.api.exception.ExamplePromptNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationResponseDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationCategoryEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptCategorizationCategoryRepository;
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
  private final ExamplePromptCategorizationCategoryRepository categoryRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** Response from the AI service /categorize-prompt endpoint. */
  private record AiCategorizationResponse(String prompt, List<String> categories, String method) {}

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
   * Calls the AI service to categorize a prompt and persists the result if the classifier
   * matched at least one category. Returns the persisted row, or an empty Optional when the
   * classifier did not assign any category (no row is persisted in that case).
   *
   * <p>Only the first AI categorization auto-becomes the effective categorization (compare-and-set).
   * Subsequent AI re-runs are persisted but do not override the effective categorization.
   */
  @Transactional
  public Optional<ExamplePromptCategorizationResponseDto> categorizePrompt(UUID promptId) {
    ExamplePromptEntity prompt = getPromptOrThrow(promptId);

    try {
      String serviceToken = serviceTokenProvider.obtainServiceToken();
      AiCategorizationResponse aiResponse = callAiService(serviceToken, prompt.getQuestion());

      if (aiResponse.categories() == null || aiResponse.categories().isEmpty()) {
        log.info("AI returned no categories for prompt {} — nothing persisted", promptId);
        return Optional.empty();
      }

      ExamplePromptCategorizationEntity entity = persistCategorization(
        promptId,
        aiResponse.method(),
        aiResponse.categories(),
        null,
        null
      );

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

      log.info(
        "Prompt categorization persisted for prompt {}: method={}, categories={}",
        promptId,
        aiResponse.method(),
        aiResponse.categories()
      );
      return Optional.of(toDto(entity));
    } catch (Exception e) {
      throw new IllegalStateException(
        "Prompt categorization failed for prompt " + promptId + ": " + e.getMessage(),
        e
      );
    }
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

    List<String> categorySlugs = request
      .getCategories()
      .stream()
      .map(BiomedicalCategoryDto::getValue)
      .toList();

    ExamplePromptCategorizationEntity entity = persistCategorization(
      promptId,
      "human-review",
      categorySlugs,
      categorizedBy,
      request.getReason()
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
    List<BiomedicalCategoryDto> categories = categoryRepository
      .findByCategorizationId(entity.getId())
      .stream()
      .map(c -> BiomedicalCategoryDto.fromValue(c.getCategory()))
      .toList();

    ExamplePromptCategorizationResponseDto dto = new ExamplePromptCategorizationResponseDto();
    dto.setId(entity.getId());
    dto.setPromptId(entity.getPromptId());
    dto.setCategories(categories);
    dto.setMethod(entity.getMethod());
    dto.setCategorizedBy(entity.getCategorizedBy());
    dto.setReason(entity.getReason());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }

  private ExamplePromptCategorizationEntity persistCategorization(
    UUID promptId,
    String method,
    List<String> categorySlugs,
    UUID categorizedBy,
    String reason
  ) {
    ExamplePromptCategorizationEntity entity = ExamplePromptCategorizationEntity.builder()
      .promptId(promptId)
      .method(method)
      .categorizedBy(categorizedBy)
      .reason(reason)
      .build();

    categorizationRepository.save(entity);
    categorizationRepository.flush();

    List<ExamplePromptCategorizationCategoryEntity> categoryEntities = categorySlugs
      .stream()
      .map(slug ->
        ExamplePromptCategorizationCategoryEntity.builder()
          .categorizationId(entity.getId())
          .category(slug)
          .build()
      )
      .toList();
    categoryRepository.saveAll(categoryEntities);
    categoryRepository.flush();

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
