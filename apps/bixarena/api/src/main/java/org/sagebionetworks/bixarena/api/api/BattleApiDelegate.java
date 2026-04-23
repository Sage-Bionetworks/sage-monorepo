package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCategorizationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCategorizationResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.BattlePageDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelChatCompletionChunkDto;
import org.sagebionetworks.bixarena.api.model.dto.RateLimitErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.SetEffectiveCategorizationRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.SetEffectiveValidationRequestDto;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link BattleApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface BattleApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /battles : Create a battle
     * Create a new battle between two AI models.
     *
     * @param battleCreateRequestDto  (required)
     * @return Battle created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattle
     */
    default ResponseEntity<BattleCreateResponseDto> createBattle(BattleCreateRequestDto battleCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"model2\" : { \"externalLink\" : \"https://openrouter.ai/models/openai/gpt-4\", \"active\" : true, \"description\" : \"A large multimodal model that can process text and images.\", \"apiBase\" : \"https://openrouter.ai/api/v1\", \"apiModelName\" : \"anthropic/claude-sonnet-4.5\", \"license\" : \"open-source\", \"createdAt\" : \"2025-09-15T12:00:00Z\", \"organization\" : \"OpenAI\", \"name\" : \"My Awesome Model\", \"alias\" : \"awesome-model-v2\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"slug\" : \"my-awesome-model\", \"updatedAt\" : \"2025-09-20T08:30:00Z\" }, \"model1\" : { \"externalLink\" : \"https://openrouter.ai/models/openai/gpt-4\", \"active\" : true, \"description\" : \"A large multimodal model that can process text and images.\", \"apiBase\" : \"https://openrouter.ai/api/v1\", \"apiModelName\" : \"anthropic/claude-sonnet-4.5\", \"license\" : \"open-source\", \"createdAt\" : \"2025-09-15T12:00:00Z\", \"organization\" : \"OpenAI\", \"name\" : \"My Awesome Model\", \"alias\" : \"awesome-model-v2\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"slug\" : \"my-awesome-model\", \"updatedAt\" : \"2025-09-20T08:30:00Z\" }, \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/categorizations : Create a battle categorization
     * Manually categorize a battle. The created categorization is automatically set as the effective categorization.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param battleCategorizationCreateRequestDto  (required)
     * @return Battle categorization created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattleCategorization
     */
    default ResponseEntity<BattleCategorizationResponseDto> createBattleCategorization(UUID battleId,
        BattleCategorizationCreateRequestDto battleCategorizationCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"categorizedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"categories\" : [ \"genetics\", \"genetics\", \"genetics\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/evaluations : Create a battle evaluation
     * Record the outcome of a battle.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param battleEvaluationCreateRequestDto  (required)
     * @return BattleEvaluation created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattleEvaluation
     */
    default ResponseEntity<BattleEvaluationDto> createBattleEvaluation(UUID battleId,
        BattleEvaluationCreateRequestDto battleEvaluationCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"battleId\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"outcome\" : \"model1\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/rounds : Create a battle round
     * Create a new round for a given battle.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param battleRoundCreateRequestDto  (required)
     * @return Battle round created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattleRound
     */
    default ResponseEntity<BattleRoundDto> createBattleRound(UUID battleId,
        BattleRoundCreateRequestDto battleRoundCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"roundNumber\" : 1, \"battleId\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"model2MessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"promptMessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"model1MessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"id\" : \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"updatedAt\" : \"2024-01-15T10:45:00Z\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/rounds/{roundId}/stream : Stream a chat completion for a battle round
     * Sends the round&#39;s prompt and conversation history to the specified model via ai-service and returns the response as a streaming HTTP response (text/event-stream). The completed response is persisted as a message after the stream ends.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param roundId The unique identifier of the battle round (required)
     * @param modelId The model to stream (required)
     * @return Streaming HTTP response with chat completion chunks (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattleRoundCompletion
     */
    default ResponseEntity<ModelChatCompletionChunkDto> createBattleRoundCompletion(UUID battleId,
        UUID roundId,
        UUID modelId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("text/event-stream"))) {
                    String exampleString = "Custom MIME type example not yet supported: text/event-stream";
                    ApiUtil.setExampleResponse(request, "text/event-stream", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/validations : Create a battle validation
     * Manually validate or invalidate a battle (admin only). The created validation is automatically set as the effective validation for the battle.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param battleValidationCreateRequestDto  (required)
     * @return Battle validation created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattleValidation
     */
    default ResponseEntity<BattleValidationResponseDto> createBattleValidation(UUID battleId,
        BattleValidationCreateRequestDto battleValidationCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"confidence\" : 0.08008282, \"validatedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"isBiomedical\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /battles/{battleId} : Delete a battle
     * Delete a battle by its unique identifier
     *
     * @param battleId The unique identifier of the battle (required)
     * @return Battle deleted successfully (status code 204)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#deleteBattle
     */
    default ResponseEntity<Void> deleteBattle(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /battles/{battleId} : Get a battle by ID
     * Returns a single battle by its unique identifier
     *
     * @param battleId The unique identifier of the battle (required)
     * @return Success (status code 200)
     *         or Unauthorized (status code 401)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#getBattle
     */
    default ResponseEntity<BattleDto> getBattle(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /battles/{battleId}/categorizations : List battle categorizations
     * Get all categorizations for a battle.
     *
     * @param battleId The unique identifier of the battle (required)
     * @return List of battle categorizations (status code 200)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#listBattleCategorizations
     */
    default ResponseEntity<List<BattleCategorizationResponseDto>> listBattleCategorizations(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"categorizedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"categories\" : [ \"genetics\", \"genetics\", \"genetics\" ] }, { \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"categorizedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"categories\" : [ \"genetics\", \"genetics\", \"genetics\" ] } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /battles/{battleId}/validations : List battle validations
     * Get all validations for a battle (admin only).
     *
     * @param battleId The unique identifier of the battle (required)
     * @return List of battle validations (status code 200)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#listBattleValidations
     */
    default ResponseEntity<List<BattleValidationResponseDto>> listBattleValidations(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"confidence\" : 0.08008282, \"validatedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"isBiomedical\" : true }, { \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"confidence\" : 0.08008282, \"validatedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"isBiomedical\" : true } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /battles : List battles
     * List battles with optional filtering and pagination
     *
     * @param battleSearchQuery The search query used to find and filter battles. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#listBattles
     */
    default ResponseEntity<BattlePageDto> listBattles(BattleSearchQueryDto battleSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"battles\" : [ { \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }, { \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" } ], \"page\" : { \"number\" : 99, \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/categorizations/run : Run an automated categorization
     * Run an automated AI categorization against a battle. Always returns 201 with the persisted row — when the AI could not match any category, the row is still persisted with an empty &#x60;categories&#x60; array to preserve the audit trail and avoid redundant re-classification. Returns 409 if the battle is not biomedical.
     *
     * @param battleId The unique identifier of the battle (required)
     * @return Categorization completed and persisted successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#runBattleCategorization
     */
    default ResponseEntity<BattleCategorizationResponseDto> runBattleCategorization(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"categorizedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"categories\" : [ \"genetics\", \"genetics\", \"genetics\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /battles/{battleId}/validations/run : Run an automated validation method
     * Run an automated validation method against a battle and return the result. Useful for backfilling validations on battles created before automated validation was implemented. Admin only.
     *
     * @param battleId The unique identifier of the battle (required)
     * @return Validation completed and persisted successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#runBattleValidation
     */
    default ResponseEntity<BattleValidationResponseDto> runBattleValidation(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reason\" : \"reason\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"battleId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"method\" : \"method\", \"confidence\" : 0.08008282, \"validatedBy\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"isBiomedical\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /battles/{battleId}/categorizations/effective : Set effective battle categorization
     * Set or clear the effective categorization for a battle by pointing at a row from history. Pass null to clear. Returns 409 if the battle is not biomedical — the gate applies to both setting and clearing; non-biomedical battles are not eligible for any effective categorization state change.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param setEffectiveCategorizationRequestDto  (required)
     * @return Effective categorization updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#setEffectiveBattleCategorization
     */
    default ResponseEntity<BattleDto> setEffectiveBattleCategorization(UUID battleId,
        SetEffectiveCategorizationRequestDto setEffectiveCategorizationRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /battles/{battleId}/validations/effective : Set effective validation
     * Set or clear the effective validation for a battle. The effective validation determines whether a battle counts in stats (only battles with a positive effective validation are included). Admin only.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param setEffectiveValidationRequestDto  (required)
     * @return Effective validation updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#setEffectiveValidation
     */
    default ResponseEntity<BattleDto> setEffectiveValidation(UUID battleId,
        SetEffectiveValidationRequestDto setEffectiveValidationRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /battles/{battleId} : Update a battle
     * Update a battle&#39;s end time or other properties
     *
     * @param battleId The unique identifier of the battle (required)
     * @param battleUpdateRequestDto  (required)
     * @return Battle updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#updateBattle
     */
    default ResponseEntity<BattleDto> updateBattle(UUID battleId,
        BattleUpdateRequestDto battleUpdateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"effectiveValidationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"effectiveCategorizationId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /battles/{battleId}/rounds/{roundId} : Update a battle round
     * Update an existing battle round.
     *
     * @param battleId The unique identifier of the battle (required)
     * @param roundId The unique identifier of the battle round (required)
     * @param battleRoundUpdateRequestDto  (required)
     * @return Battle round updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#updateBattleRound
     */
    default ResponseEntity<BattleRoundDto> updateBattleRound(UUID battleId,
        UUID roundId,
        BattleRoundUpdateRequestDto battleRoundUpdateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"roundNumber\" : 1, \"battleId\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"model2MessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"promptMessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"model1MessageId\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\", \"id\" : \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"updatedAt\" : \"2024-01-15T10:45:00Z\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"retryAfterSeconds\" : 18, \"limit\" : 100, \"detail\" : \"detail\", \"window\" : \"1 minute\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
