package org.sagebionetworks.agora.api.next.api;

import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
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
 * A delegate to be called by the {@link NominatedDrugApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface NominatedDrugApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/drugs : List Nominated Drugs
     * Retrieve the list of nominated drugs
     *
     * @return Successfully retrieved nominated drugs (status code 200)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see NominatedDrugApi#listNominatedDrugs
     */
    default ResponseEntity<List<NominatedDrugDto>> listNominatedDrugs() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"total_nominations\" : 1, \"year_first_nominated\" : 2025, \"principal_investigators\" : [ \"Xie\" ], \"programs\" : [ \"ACTDRx AD\" ], \"common_name\" : \"Agomelatine\" }, { \"total_nominations\" : 1, \"year_first_nominated\" : 2025, \"principal_investigators\" : [ \"Xie\" ], \"programs\" : [ \"ACTDRx AD\" ], \"common_name\" : \"Agomelatine\" } ]";
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
