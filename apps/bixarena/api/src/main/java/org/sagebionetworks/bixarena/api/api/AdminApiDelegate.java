package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.AdminStats200ResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestDto;
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
 * A delegate to be called by the {@link AdminApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface AdminApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /admin/quests/{questId} : Get a quest (admin, ungated)
     * Get the full quest with ALL posts and full content. No publish date filtering, no progress or tier gating. Requires admin role. Use this endpoint for content management; use GET /quests/{questId} to experience the quest as a user. 
     *
     * @param questId Unique identifier for a quest (required)
     * @return Success (status code 200)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AdminApi#adminGetQuest
     */
    default ResponseEntity<QuestDto> adminGetQuest(String questId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"questId\" : \"build-bioarena-together\", \"goal\" : 2850, \"endDate\" : \"2026-04-30T23:59:59Z\", \"activePostIndex\" : 4, \"description\" : \"Join forces to build an arena together...\", \"title\" : \"Build BioArena Together\", \"posts\" : [ { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"locked\" : false, \"requiredTier\" : \"knight\" }, { \"date\" : \"2026-02-03\", \"images\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"postIndex\" : 0, \"publishDate\" : \"2026-03-16T09:00:00Z\", \"description\" : \"The arena walls began to rise...\", \"requiredProgress\" : 500, \"title\" : \"Chapter 1: Laying the First Stones\", \"locked\" : false, \"requiredTier\" : \"knight\" } ], \"startDate\" : \"2026-02-01T00:00:00Z\", \"totalBlocks\" : 150 }";
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
     * GET /admin/stats : Admin statistics
     * Administrative operations requiring admin role.
     *
     * @return Success (status code 200)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AdminApi#adminStats
     */
    default ResponseEntity<AdminStats200ResponseDto> adminStats() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"ok\" : true }";
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

}
