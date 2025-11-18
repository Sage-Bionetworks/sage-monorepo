package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardListInnerDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.RateLimitErrorDto;
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
 * A delegate to be called by the {@link LeaderboardApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface LeaderboardApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /leaderboards/{leaderboardId} : Get leaderboard entries
     * Get paginated leaderboard entries for a specific leaderboard
     *
     * @param leaderboardId The unique identifier of a leaderboard (required)
     * @param leaderboardSearchQuery The search query used to find and filter leaderboard entries. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see LeaderboardApi#getLeaderboard
     */
    default ResponseEntity<LeaderboardEntryPageDto> getLeaderboard(String leaderboardId,
        LeaderboardSearchQueryDto leaderboardSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"entries\" : [ { \"modelName\" : \"GPT-4o\", \"license\" : \"MIT\", \"createdAt\" : \"2025-08-16T10:30:00Z\", \"bootstrapQ025\" : 887, \"modelId\" : \"model_456\", \"btScore\" : 0.925, \"bootstrapQ975\" : 1063, \"rank\" : 1, \"id\" : \"entry_123\", \"voteCount\" : 1250 }, { \"modelName\" : \"GPT-4o\", \"license\" : \"MIT\", \"createdAt\" : \"2025-08-16T10:30:00Z\", \"bootstrapQ025\" : 887, \"modelId\" : \"model_456\", \"btScore\" : 0.925, \"bootstrapQ975\" : 1063, \"rank\" : 1, \"id\" : \"entry_123\", \"voteCount\" : 1250 } ], \"snapshotId\" : \"snapshot_2025-08-16_14-30\", \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99, \"updatedAt\" : \"2025-08-16T14:30:00Z\" }";
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
     * GET /leaderboards/{leaderboardId}/snapshots : Get public leaderboard snapshots
     * Get a paginated list of public snapshots for a leaderboard.
     *
     * @param leaderboardId The unique identifier of a leaderboard (required)
     * @param leaderboardSnapshotQuery The query used to filter and paginate leaderboard snapshots. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see LeaderboardApi#getLeaderboardSnapshots
     */
    default ResponseEntity<LeaderboardSnapshotPageDto> getLeaderboardSnapshots(String leaderboardId,
        LeaderboardSnapshotQueryDto leaderboardSnapshotQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"snapshots\" : [ { \"createdAt\" : \"2025-08-16T14:30:00Z\", \"entryCount\" : 50, \"visibility\" : \"public\", \"description\" : \"Weekly evaluation run\", \"id\" : \"snapshot_2025-08-16_14-30\" }, { \"createdAt\" : \"2025-08-16T14:30:00Z\", \"entryCount\" : 50, \"visibility\" : \"public\", \"description\" : \"Weekly evaluation run\", \"id\" : \"snapshot_2025-08-16_14-30\" } ], \"number\" : 99, \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 }";
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
     * GET /leaderboards/{leaderboardId}/history/{modelId} : Get model performance history
     * Get historical performance data for a specific model in a leaderboard
     *
     * @param leaderboardId The unique identifier of a leaderboard (required)
     * @param modelId The unique identifier of a model (required)
     * @param leaderboardModelHistoryQuery The query used to filter and paginate historical model performance data. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see LeaderboardApi#getModelHistory
     */
    default ResponseEntity<LeaderboardModelHistoryPageDto> getModelHistory(String leaderboardId,
        String modelId,
        LeaderboardModelHistoryQueryDto leaderboardModelHistoryQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"modelName\" : \"GPT-4o\", \"size\" : 99, \"modelId\" : \"model_456\", \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"history\" : [ { \"createdAt\" : \"2025-08-15T10:00:00Z\", \"bootstrapQ025\" : 875, \"snapshotId\" : \"snapshot_2025-08-15_10-00\", \"btScore\" : 0.915, \"bootstrapQ975\" : 1055, \"rank\" : 2, \"voteCount\" : 1180 }, { \"createdAt\" : \"2025-08-15T10:00:00Z\", \"bootstrapQ025\" : 875, \"snapshotId\" : \"snapshot_2025-08-15_10-00\", \"btScore\" : 0.915, \"bootstrapQ975\" : 1055, \"rank\" : 2, \"voteCount\" : 1180 } ], \"totalElements\" : 99 }";
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
     * GET /leaderboards : List all available leaderboards
     * Get a list of all available leaderboards with their metadata
     *
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see LeaderboardApi#listLeaderboards
     */
    default ResponseEntity<List<LeaderboardListInnerDto>> listLeaderboards() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"name\" : \"Open Source Models\", \"description\" : \"Performance ranking of open-source AI models\", \"id\" : \"open-source\", \"updatedAt\" : \"2025-08-16T14:30:00Z\" }, { \"name\" : \"Open Source Models\", \"description\" : \"Performance ranking of open-source AI models\", \"id\" : \"open-source\", \"updatedAt\" : \"2025-08-16T14:30:00Z\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
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
