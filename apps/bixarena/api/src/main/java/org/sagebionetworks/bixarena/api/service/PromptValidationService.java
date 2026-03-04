package org.sagebionetworks.bixarena.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.model.entity.PromptValidationEntity;
import org.sagebionetworks.bixarena.api.model.repository.PromptValidationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptValidationService {

  /** Refresh the token 30 seconds before it actually expires. */
  private static final long TOKEN_REFRESH_MARGIN_SECONDS = 30;

  private final PromptValidationRepository promptValidationRepository;
  private final AppProperties appProperties;
  private final ObjectMapper objectMapper;

  private final HttpClient httpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_1_1)
    .build();

  /** Cached service token and its expiration time. */
  private volatile String cachedToken;
  private volatile Instant tokenExpiresAt = Instant.EPOCH;

  /** Response from the auth service /oauth2/service-token endpoint. */
  private record ServiceTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") int expiresIn
  ) {}

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
  @Transactional
  public void validateAndPersist(UUID battleId, UUID messageId, String promptText) {
    try {
      String serviceToken = obtainServiceToken();
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

  private synchronized String obtainServiceToken() throws Exception {
    // Return cached token if still valid
    if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
      return cachedToken;
    }

    String clientId = appProperties.authService().serviceClientId();
    String clientSecret = appProperties.authService().serviceClientSecret();
    String credentials = clientId + ":" + clientSecret;
    String encoded = Base64.getEncoder()
      .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

    String url = appProperties.authService().baseUrl()
      + "/oauth2/service-token?audience=urn:bixarena:ai";

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header("Authorization", "Basic " + encoded)
      .POST(HttpRequest.BodyPublishers.noBody())
      .build();

    HttpResponse<String> httpResponse = httpClient.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (httpResponse.statusCode() != 200) {
      throw new IllegalStateException(
        "Auth service returned " + httpResponse.statusCode() + ": " + httpResponse.body()
      );
    }

    ServiceTokenResponse response = objectMapper.readValue(
      httpResponse.body(),
      ServiceTokenResponse.class
    );

    cachedToken = response.accessToken();
    tokenExpiresAt = Instant.now()
      .plusSeconds(response.expiresIn() - TOKEN_REFRESH_MARGIN_SECONDS);
    log.debug("Cached service token, expires at {}", tokenExpiresAt);

    return cachedToken;
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

    HttpResponse<String> httpResponse = httpClient.send(
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
