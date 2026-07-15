package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewsPageDto;
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
 * A delegate to be called by the {@link MarmosetModelOverviewApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface MarmosetModelOverviewApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/marmoset-model-overview : Get marmoset model overview for comparison tools
     * Returns a paginated list of marmoset model overview objects for use in comparison tools.
     *
     * @param marmosetModelOverviewSearchQuery The search query used to find and filter marmoset model overviews. (optional)
     * @return A paginated list of marmoset model overview objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see MarmosetModelOverviewApi#getMarmosetModelOverviews
     */
    default ResponseEntity<MarmosetModelOverviewsPageDto> getMarmosetModelOverviews(MarmosetModelOverviewSearchQueryDto marmosetModelOverviewSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"marmosetModelOverviews\" : [ { \"biomarkers\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"name\" : \"name\", \"model_type\" : \"model_type\", \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"_id\" : \"_id\", \"study_data\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"available_data\" : [ \"Plasma Biomarkers\", \"Plasma Biomarkers\" ] }, { \"biomarkers\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"name\" : \"name\", \"model_type\" : \"model_type\", \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"_id\" : \"_id\", \"study_data\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"available_data\" : [ \"Plasma Biomarkers\", \"Plasma Biomarkers\" ] } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
