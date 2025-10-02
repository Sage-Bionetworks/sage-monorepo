package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSearchQueryDto;
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
 * A delegate to be called by the {@link ModelApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ModelApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /models : List models
     * Get a paginated list of models with optional filters (e.g., active)
     *
     * @param modelSearchQuery The search query used to find and filter models. (optional)
     * @return Success (status code 200)
     *         or Invalid request parameters (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ModelApi#listModels
     */
    default ResponseEntity<ModelPageDto> listModels(ModelSearchQueryDto modelSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"models\" : [ { \"externalLink\" : \"https://openrouter.ai/models/openai/gpt-4\", \"active\" : true, \"description\" : \"A large multimodal model that can process text and images.\", \"apiBase\" : \"https://openrouter.ai/api/v1\", \"apiModelName\" : \"anthropic/claude-sonnet-4.5\", \"license\" : \"open-source\", \"createdAt\" : \"2025-09-15T12:00:00Z\", \"organization\" : \"OpenAI\", \"name\" : \"My Awesome Model\", \"alias\" : \"awesome-model-v2\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"slug\" : \"my-awesome-model\", \"updatedAt\" : \"2025-09-20T08:30:00Z\" }, { \"externalLink\" : \"https://openrouter.ai/models/openai/gpt-4\", \"active\" : true, \"description\" : \"A large multimodal model that can process text and images.\", \"apiBase\" : \"https://openrouter.ai/api/v1\", \"apiModelName\" : \"anthropic/claude-sonnet-4.5\", \"license\" : \"open-source\", \"createdAt\" : \"2025-09-15T12:00:00Z\", \"organization\" : \"OpenAI\", \"name\" : \"My Awesome Model\", \"alias\" : \"awesome-model-v2\", \"id\" : \"5f6c2d84-5c1a-4b2e-b3d7-0c2a1f9e8a6f\", \"slug\" : \"my-awesome-model\", \"updatedAt\" : \"2025-09-20T08:30:00Z\" } ], \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 }";
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
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
