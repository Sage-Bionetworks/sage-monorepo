package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestContributorsDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostReorderDto;
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
 * A delegate to be called by the {@link QuestApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface QuestApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /quests : Create a quest
     * Create a new community quest. Requires admin role.
     *
     * @param questCreateOrUpdateDto  (required)
     * @return Quest created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#createQuest
     */
    default ResponseEntity<QuestDto> createQuest(QuestCreateOrUpdateDto questCreateOrUpdateDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"goal\" : 2850, \"endDate\" : \"2026-04-30T23:59:59Z\", \"activePostIndex\" : 4, \"description\" : \"Join forces to build an arena together...\", \"title\" : \"Build BioArena Together\", \"posts\" : [ { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }, { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" } ], \"startDate\" : \"2026-02-01T00:00:00Z\", \"totalBlocks\" : 150 }";
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
     * POST /quests/{questId}/posts : Create a quest post
     * Add a new post to a quest. The post is appended at the next available post index. Requires admin role. 
     *
     * @param questId Unique identifier for a quest (required)
     * @param questPostCreateOrUpdateDto  (required)
     * @return Post created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#createQuestPost
     */
    default ResponseEntity<QuestPostDto> createQuestPost(String questId,
        QuestPostCreateOrUpdateDto questPostCreateOrUpdateDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }";
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
     * DELETE /quests/{questId} : Delete a quest
     * Delete a quest and all its posts. Requires admin role.
     *
     * @param questId Unique identifier for a quest (required)
     * @return Quest deleted successfully (status code 204)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#deleteQuest
     */
    default ResponseEntity<Void> deleteQuest(String questId) {
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
     * DELETE /quests/{questId}/posts/{postIndex} : Delete a quest post
     * Delete a quest post. Remaining posts are not automatically reindexed. Requires admin role.
     *
     * @param questId Unique identifier for a quest (required)
     * @param postIndex Display ordering index of a quest post (0-based) (required)
     * @return Post deleted successfully (status code 204)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#deleteQuestPost
     */
    default ResponseEntity<Void> deleteQuestPost(String questId,
        Integer postIndex) {
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
     * GET /quests/{questId} : Get a quest
     * Get the full quest configuration including metadata and published posts. Post content (description, images) is filtered based on unlock gates: - Posts before their publish date are excluded entirely. - Posts whose progress or tier gate is not met return metadata only   (title, date, requiredTier, requiredProgress) with null description   and empty images. When authenticated, the caller&#39;s contributor tier is resolved from their battle count during the quest period. 
     *
     * @param questId Unique identifier for a quest (required)
     * @return Success (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#getQuest
     */
    default ResponseEntity<QuestDto> getQuest(String questId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"goal\" : 2850, \"endDate\" : \"2026-04-30T23:59:59Z\", \"activePostIndex\" : 4, \"description\" : \"Join forces to build an arena together...\", \"title\" : \"Build BioArena Together\", \"posts\" : [ { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }, { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" } ], \"startDate\" : \"2026-02-01T00:00:00Z\", \"totalBlocks\" : 150 }";
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

    /**
     * GET /quests/{questId}/contributors : Get quest contributors
     * Get a list of users who have contributed to a specific quest, ordered by their battle count during the quest period. Results are calculated in real-time based on completed battles within the quest&#39;s start and end dates. 
     *
     * @param questId Unique identifier for a quest (required)
     * @param minBattles Minimum number of battles required to be listed (optional, default to 1)
     * @param limit Maximum number of contributors to return (optional, default to 100)
     * @return Success (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#listQuestContributors
     */
    default ResponseEntity<QuestContributorsDto> listQuestContributors(String questId,
        Integer minBattles,
        Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"endDate\" : \"2026-04-20T23:59:59Z\", \"contributors\" : [ { \"tier\" : \"champion\", \"battlesPerWeek\" : 12.5, \"battleCount\" : 42, \"username\" : \"tschaffter\" }, { \"tier\" : \"champion\", \"battlesPerWeek\" : 12.5, \"battleCount\" : 42, \"username\" : \"tschaffter\" } ], \"startDate\" : \"2026-01-20T00:00:00Z\", \"totalContributors\" : 42 }";
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

    /**
     * PUT /quests/{questId}/posts/reorder : Reorder quest posts
     * Reorder all posts in a quest. The request body must contain the complete list of existing post indexes in the desired new order. The backend validates that the array contains exactly all existing post indexes (no duplicates, no missing), then reassigns post_index values 0, 1, 2, ... based on array order. Requires admin role. 
     *
     * @param questId Unique identifier for a quest (required)
     * @param questPostReorderDto  (required)
     * @return Posts reordered successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#reorderQuestPosts
     */
    default ResponseEntity<QuestDto> reorderQuestPosts(String questId,
        QuestPostReorderDto questPostReorderDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"goal\" : 2850, \"endDate\" : \"2026-04-30T23:59:59Z\", \"activePostIndex\" : 4, \"description\" : \"Join forces to build an arena together...\", \"title\" : \"Build BioArena Together\", \"posts\" : [ { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }, { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" } ], \"startDate\" : \"2026-02-01T00:00:00Z\", \"totalBlocks\" : 150 }";
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
     * PUT /quests/{questId} : Update a quest
     * Update quest metadata. Requires admin role.
     *
     * @param questId Unique identifier for a quest (required)
     * @param questCreateOrUpdateDto  (required)
     * @return Quest updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#updateQuest
     */
    default ResponseEntity<QuestDto> updateQuest(String questId,
        QuestCreateOrUpdateDto questCreateOrUpdateDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"goal\" : 2850, \"endDate\" : \"2026-04-30T23:59:59Z\", \"activePostIndex\" : 4, \"description\" : \"Join forces to build an arena together...\", \"title\" : \"Build BioArena Together\", \"posts\" : [ { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }, { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" } ], \"startDate\" : \"2026-02-01T00:00:00Z\", \"totalBlocks\" : 150 }";
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
     * PUT /quests/{questId}/posts/{postIndex} : Update a quest post
     * Update an existing quest post. Requires admin role.
     *
     * @param questId Unique identifier for a quest (required)
     * @param postIndex Display ordering index of a quest post (0-based) (required)
     * @param questPostCreateOrUpdateDto  (required)
     * @return Post updated successfully (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see QuestApi#updateQuestPost
     */
    default ResponseEntity<QuestPostDto> updateQuestPost(String questId,
        Integer postIndex,
        QuestPostCreateOrUpdateDto questPostCreateOrUpdateDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"requiredTier\" : \"knight\" }";
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
