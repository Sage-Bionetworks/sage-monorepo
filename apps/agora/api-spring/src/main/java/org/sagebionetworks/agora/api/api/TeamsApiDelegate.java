package org.sagebionetworks.agora.api.api;

import org.sagebionetworks.agora.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.model.dto.TeamsListDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link TeamsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface TeamsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /teamMembers/{name}/image : Get Team Member Image
     * Get Team Member Image
     *
     * @param name  (required)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see TeamsApi#getTeamMemberImage
     */
    default ResponseEntity<org.springframework.core.io.Resource> getTeamMemberImage(String name) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /teams : List Teams
     * List Teams
     *
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see TeamsApi#listTeams
     */
    default ResponseEntity<TeamsListDto> listTeams() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"items\" : [ { \"team_full\" : \"team_full\", \"members\" : [ { \"name\" : \"name\", \"isPrimaryInvestigator\" : true, \"url\" : \"url\" }, { \"name\" : \"name\", \"isPrimaryInvestigator\" : true, \"url\" : \"url\" } ], \"description\" : \"description\", \"team\" : \"team\", \"program\" : \"program\" }, { \"team_full\" : \"team_full\", \"members\" : [ { \"name\" : \"name\", \"isPrimaryInvestigator\" : true, \"url\" : \"url\" }, { \"name\" : \"name\", \"isPrimaryInvestigator\" : true, \"url\" : \"url\" } ], \"description\" : \"description\", \"team\" : \"team\", \"program\" : \"program\" } ] }";
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
