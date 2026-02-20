package org.sagebionetworks.agora.api.next.api;

import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
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
 * A delegate to be called by the {@link NominatedTargetApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface NominatedTargetApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/targets : Get nominated target results for comparison tools
     * Returns a paginated list of nominated target results for use in comparison tools.
     *
     * @param nominatedTargetSearchQuery The search query used to find and filter nominated targets. (optional)
     * @return A paginated list of nominated target results (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see NominatedTargetApi#getNominatedTargets
     */
    default ResponseEntity<NominatedTargetsPageDto> getNominatedTargets(NominatedTargetSearchQueryDto nominatedTargetSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"nominatedTargets\" : [ { \"input_data\" : [ \"RNA\", \"Protein\" ], \"nominating_teams\" : [ \"Duke\", \"Emory\" ], \"initial_nomination\" : 2021, \"cohort_studies\" : [ \"ROSMAP\", \"Mayo\" ], \"ensembl_gene_id\" : \"ENSG00000141510\", \"total_nominations\" : 5, \"hgnc_symbol\" : \"TP53\", \"pharos_class\" : \"Tbio\", \"_id\" : \"_id\", \"programs\" : [ \"AMP-AD\", \"Community\" ] }, { \"input_data\" : [ \"RNA\", \"Protein\" ], \"nominating_teams\" : [ \"Duke\", \"Emory\" ], \"initial_nomination\" : 2021, \"cohort_studies\" : [ \"ROSMAP\", \"Mayo\" ], \"ensembl_gene_id\" : \"ENSG00000141510\", \"total_nominations\" : 5, \"hgnc_symbol\" : \"TP53\", \"pharos_class\" : \"Tbio\", \"_id\" : \"_id\", \"programs\" : [ \"AMP-AD\", \"Community\" ] } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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

}
