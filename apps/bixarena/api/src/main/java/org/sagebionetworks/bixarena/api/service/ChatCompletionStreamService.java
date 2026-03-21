package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.BattleRoundNotFoundException;
import org.sagebionetworks.bixarena.api.exception.ModelNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.MessageCreateDto;
import org.sagebionetworks.bixarena.api.model.dto.MessageRoleDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelErrorEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.sagebionetworks.bixarena.api.model.repository.ModelErrorRepository;
import org.sagebionetworks.bixarena.api.model.repository.ModelRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Streams a chat completion for a battle round by proxying ai-service SSE
 * and persisting the completed response.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatCompletionStreamService {

  private final BattleRepository battleRepository;
  private final BattleRoundRepository battleRoundRepository;
  private final ModelRepository modelRepository;
  private final MessageRepository messageRepository;
  private final MessageService messageService;
  private final ModelErrorRepository modelErrorRepository;
  private final ServiceTokenProvider serviceTokenProvider;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  /** SSE chunk from ai-service. */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  record AiChunk(
    String content,
    String status,
    @JsonProperty("finishReason") String finishReason,
    @JsonProperty("errorMessage") String errorMessage,
    AiUsage usage
  ) {}

  @JsonInclude(JsonInclude.Include.NON_NULL)
  record AiUsage(
    String model,
    Float temperature,
    @JsonProperty("topP") Float topP,
    @JsonProperty("maxTokens") Integer maxTokens,
    @JsonProperty("promptTokens") Integer promptTokens,
    @JsonProperty("completionTokens") Integer completionTokens,
    @JsonProperty("totalTokens") Integer totalTokens
  ) {}

  /** Request payload for ai-service POST /chat/completions. */
  record AiChatRequest(
    UUID modelId,
    String apiModelName,
    String apiBase,
    List<Map<String, String>> messages
  ) {}

  /**
   * Streams chat completion chunks from ai-service directly to the HTTP response,
   * then persists the accumulated response as a message on the battle round.
   */
  public void streamCompletion(
      UUID battleId, UUID roundId, UUID modelId, UUID callerId, HttpServletResponse response) {
    BattleEntity battle = battleRepository
      .findById(battleId)
      .orElseThrow(() -> new BattleNotFoundException(
        String.format("The battle with ID %s does not exist.", battleId)));

    // Ownership check: users can only stream their own battles
    if (!battle.getUserId().equals(callerId)) {
      log.warn("User {} attempted to stream battle {} owned by {}", callerId, battleId, battle.getUserId());
      throw new org.springframework.security.access.AccessDeniedException(
        "You can only stream your own battles"
      );
    }

    BattleRoundEntity round = battleRoundRepository
      .findById(roundId)
      .orElseThrow(() -> new BattleRoundNotFoundException(
        String.format("The battle round with ID %s does not exist.", roundId)));

    if (!round.getBattleId().equals(battleId)) {
      throw new BattleRoundNotFoundException(
        String.format("The battle with ID %s does not contain round %s.", battleId, roundId));
    }

    // Verify model belongs to this battle
    boolean isModel1 = modelId.equals(battle.getModel1Id());
    boolean isModel2 = modelId.equals(battle.getModel2Id());
    if (!isModel1 && !isModel2) {
      throw new ModelNotFoundException(
        String.format("Model %s does not belong to battle %s.", modelId, battleId));
    }

    ModelEntity model = modelRepository
      .findById(modelId)
      .orElseThrow(() -> new ModelNotFoundException(
        String.format("Model with id %s not found", modelId)));

    // Build conversation messages from DB
    List<Map<String, String>> messages = buildConversationMessages(battleId, round);

    try {
      doStream(response, battle, round, model, isModel1, messages);
    } catch (Exception e) {
      log.error("Stream failed for round {} model {}: {}",
        roundId, modelId, e.getMessage(), e);
      try {
        sendErrorEvent(response, "An unexpected error occurred.");
      } catch (Exception writeErr) {
        log.error("Failed to write error to response", writeErr);
      }
    }
  }

  private List<Map<String, String>> buildConversationMessages(
      UUID battleId, BattleRoundEntity currentRound) {
    List<Map<String, String>> messages = new ArrayList<>();

    // Get all rounds up to and including current, ordered by round number
    List<BattleRoundEntity> rounds = battleRoundRepository
      .findByBattleIdOrderByRoundNumberAsc(battleId);

    for (BattleRoundEntity round : rounds) {
      if (round.getRoundNumber() > currentRound.getRoundNumber()) {
        break;
      }

      // Add the user prompt
      if (round.getPromptMessageId() != null) {
        messageRepository.findById(round.getPromptMessageId()).ifPresent(msg ->
          messages.add(Map.of("role", "user", "content", msg.getContent()))
        );
      }

      // For prior rounds, add the assistant response (don't add for current round)
      if (round.getRoundNumber() < currentRound.getRoundNumber()) {
        // TODO: Which model's response to include for multi-turn? For now, skip prior responses.
        // This matches current frontend behavior where each round is independent.
      }
    }

    return messages;
  }

  private void doStream(
      HttpServletResponse response,
      BattleEntity battle,
      BattleRoundEntity round,
      ModelEntity model,
      boolean isModel1,
      List<Map<String, String>> messages
  ) throws Exception {
    String serviceToken = serviceTokenProvider.obtainServiceToken();

    AiChatRequest aiRequest = new AiChatRequest(
      model.getId(),
      model.getApiModelName(),
      model.getApiBase(),
      messages
    );

    String jsonBody = objectMapper.writeValueAsString(aiRequest);
    String url = appProperties.aiService().baseUrl() + "/chat/completions";

    log.info("Streaming chat completion: battle={}, round={}, model={}",
      battle.getId(), round.getId(), model.getId());

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header("Authorization", "Bearer " + serviceToken)
      .header("Content-Type", "application/json")
      .header("Accept", "text/event-stream")
      .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
      .build();

    HttpResponse<java.io.InputStream> httpResponse = serviceTokenProvider.getHttpClient().send(
      request,
      HttpResponse.BodyHandlers.ofInputStream()
    );

    // Set SSE headers
    response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Connection", "keep-alive");
    response.setHeader("X-Accel-Buffering", "no");

    if (httpResponse.statusCode() != 200) {
      String body = new String(httpResponse.body().readAllBytes(), StandardCharsets.UTF_8);
      log.error("AI service returned {}: {}", httpResponse.statusCode(), body);
      sendErrorEvent(response, "AI service error (code: " + httpResponse.statusCode() + ")");
      return;
    }

    StringBuilder accumulatedText = new StringBuilder();
    String errorMessage = null;

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(httpResponse.body(), StandardCharsets.UTF_8))) {
      PrintWriter writer = response.getWriter();
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.startsWith("data:")) {
          continue;
        }

        String data = line.substring(5).trim();
        if (data.isEmpty() || "[DONE]".equals(data)) {
          continue;
        }

        AiChunk chunk = objectMapper.readValue(data, AiChunk.class);

        // Forward chunk to client
        writer.write("data:" + data + "\n\n");
        writer.flush();

        // Accumulate content
        if (chunk.content() != null) {
          accumulatedText.append(chunk.content());
        }

        // Track errors
        if ("error".equals(chunk.status()) && chunk.errorMessage() != null) {
          errorMessage = chunk.errorMessage();
        }
      }
    }

    // Persist after stream ends
    persistResult(battle, round, model, isModel1, accumulatedText.toString(), errorMessage);
  }

  @Transactional
  protected void persistResult(
      BattleEntity battle,
      BattleRoundEntity round,
      ModelEntity model,
      boolean isModel1,
      String content,
      String errorMessage
  ) {
    if (errorMessage != null) {
      ModelErrorEntity error = ModelErrorEntity.builder()
        .modelId(model.getId())
        .message(errorMessage.length() > 1000 ? errorMessage.substring(0, 1000) : errorMessage)
        .battleId(battle.getId())
        .roundId(round.getId())
        .build();
      modelErrorRepository.save(error);
      log.warn("Persisted model error for model {} round {}: {}",
        model.getId(), round.getId(), errorMessage);
      return;
    }

    if (content.isBlank()) {
      log.warn("Empty response from model {} for round {}", model.getId(), round.getId());
      return;
    }

    MessageCreateDto messageDto = new MessageCreateDto();
    messageDto.setRole(MessageRoleDto.ASSISTANT);
    messageDto.setContent(content);
    UUID messageId = messageService.createMessage(messageDto);

    BattleRoundEntity freshRound = battleRoundRepository.findById(round.getId()).orElseThrow();
    if (isModel1) {
      freshRound.setModel1MessageId(messageId);
    } else {
      freshRound.setModel2MessageId(messageId);
    }
    battleRoundRepository.save(freshRound);
    battleRoundRepository.flush();

    log.info("Persisted {} message {} for round {}",
      isModel1 ? "model1" : "model2", messageId, round.getId());
  }

  private void sendErrorEvent(HttpServletResponse response, String message) throws Exception {
    if (!response.isCommitted()) {
      response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }
    AiChunk errorChunk = new AiChunk(null, "error", null, message, null);
    PrintWriter writer = response.getWriter();
    writer.write("data:" + objectMapper.writeValueAsString(errorChunk) + "\n\n");
    writer.flush();
  }
}
