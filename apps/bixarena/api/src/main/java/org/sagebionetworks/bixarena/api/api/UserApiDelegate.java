package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.RateLimitErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.UserStatsDto;
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
 * A delegate to be called by the {@link UserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface UserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /users/me/stats : Get current user&#39;s statistics
     * Returns statistics about the authenticated user&#39;s battle participation.
     *
     * @return User statistics retrieved successfully (status code 200)
     *         or Unauthorized (status code 401)
     *         or Too many requests. Rate limit exceeded. The client should wait before making additional requests. (status code 429)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see UserApi#getUserStats
     */
    default ResponseEntity<UserStatsDto> getUserStats() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"totalBattles\" : 42, \"completedBattles\" : 38, \"activeBattles\" : 4, \"firstBattleAt\" : \"2024-01-15T10:30:00Z\", \"latestBattleAt\" : \"2024-10-26T14:23:00Z\", \"rank\" : 42 }";
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
