package org.sagebionetworks.agora.api.api;

import org.sagebionetworks.agora.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.model.dto.DistributionDto;
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
 * A delegate to be called by the {@link DistributionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface DistributionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /distribution : Get distribution data
     * Get distribution data
     *
     * @return A successful response (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DistributionApi#getDistribution
     */
    default ResponseEntity<DistributionDto> getDistribution() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"overall_scores\" : [ { \"bins\" : [ [ 7.061401241503109, 7.061401241503109 ], [ 7.061401241503109, 7.061401241503109 ] ], \"name\" : \"name\", \"syn_id\" : \"syn_id\", \"distribution\" : [ 2.3021358869347655, 2.3021358869347655 ], \"wiki_id\" : \"wiki_id\" }, { \"bins\" : [ [ 7.061401241503109, 7.061401241503109 ], [ 7.061401241503109, 7.061401241503109 ] ], \"name\" : \"name\", \"syn_id\" : \"syn_id\", \"distribution\" : [ 2.3021358869347655, 2.3021358869347655 ], \"wiki_id\" : \"wiki_id\" } ], \"proteomics_SRM\" : [ { \"type\" : \"type\" }, { \"type\" : \"type\" } ], \"proteomics_TMT\" : [ { \"type\" : \"type\" }, { \"type\" : \"type\" } ], \"proteomics_LFQ\" : [ { \"type\" : \"type\" }, { \"type\" : \"type\" } ], \"rna_differential_expression\" : [ { \"min\" : 0.8008281904610115, \"first_quartile\" : 1.4658129805029452, \"median\" : 5.962133916683182, \"max\" : 6.027456183070403, \"model\" : \"model\", \"tissue\" : \"tissue\", \"_id\" : \"_id\", \"third_quartile\" : 5.637376656633329 }, { \"min\" : 0.8008281904610115, \"first_quartile\" : 1.4658129805029452, \"median\" : 5.962133916683182, \"max\" : 6.027456183070403, \"model\" : \"model\", \"tissue\" : \"tissue\", \"_id\" : \"_id\", \"third_quartile\" : 5.637376656633329 } ] }";
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
