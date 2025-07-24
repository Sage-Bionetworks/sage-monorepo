package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptsPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link EdamConceptApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface EdamConceptApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /edam-concepts : List EDAM concepts
     * List EDAM concepts
     *
     * @param edamConceptSearchQuery The search query used to find EDAM concepts. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see EdamConceptApi#listEdamConcepts
     */
    default ResponseEntity<EdamConceptsPageDto> listEdamConcepts(EdamConceptSearchQueryDto edamConceptSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"edamConcepts\" : [ { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 } ], \"totalElements\" : 99 }";
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
