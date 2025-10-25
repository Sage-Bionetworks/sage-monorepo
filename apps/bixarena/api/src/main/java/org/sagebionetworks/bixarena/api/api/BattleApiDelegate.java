package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.BattlePageDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleUpdateRequestDto;
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
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#createBattle
     */
    default ResponseEntity<BattleDto> createBattle(BattleCreateRequestDto battleCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
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
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#getBattle
     */
    default ResponseEntity<BattleDto> getBattle(UUID battleId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
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
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#listBattles
     */
    default ResponseEntity<BattlePageDto> listBattles(BattleSearchQueryDto battleSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"battles\" : [ { \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }, { \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" } ], \"page\" : { \"number\" : 99, \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 } }";
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
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BattleApi#updateBattle
     */
    default ResponseEntity<BattleDto> updateBattle(UUID battleId,
        BattleUpdateRequestDto battleUpdateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2024-01-15T10:30:00Z\", \"endedAt\" : \"2024-01-15T11:45:00Z\", \"model1Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"title\" : \"Gene Expression Analysis Comparison\", \"userId\" : \"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\", \"model2Id\" : \"1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d\" }";
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

}
