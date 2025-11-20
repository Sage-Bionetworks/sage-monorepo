package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
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
 * A delegate to be called by the {@link ModelOverviewApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ModelOverviewApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/model-overview : Get model overview for comparison tools
     * Returns a paginated list of model overview objects for use in comparison tools.
     *
     * @param modelOverviewSearchQuery The search query used to find and filter model overviews. (optional)
     * @return A paginated list of model overview objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ModelOverviewApi#getModelOverviews
     */
    default ResponseEntity<ModelOverviewsPageDto> getModelOverviews(ModelOverviewSearchQueryDto modelOverviewSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"modelOverviews\" : [ { \"pathology\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"biomarkers\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"gene_expression\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"disease_correlation\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"center\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"model_type\" : \"model_type\", \"matched_controls\" : [ \"matched_controls\", \"matched_controls\" ], \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"available_data\" : [ \"Gene Expression\", \"Gene Expression\" ], \"name\" : \"name\", \"jax_strain\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"_id\" : \"_id\", \"study_data\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" } }, { \"pathology\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"biomarkers\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"gene_expression\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"disease_correlation\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"center\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"model_type\" : \"model_type\", \"matched_controls\" : [ \"matched_controls\", \"matched_controls\" ], \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"available_data\" : [ \"Gene Expression\", \"Gene Expression\" ], \"name\" : \"name\", \"jax_strain\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"_id\" : \"_id\", \"study_data\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" } } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
