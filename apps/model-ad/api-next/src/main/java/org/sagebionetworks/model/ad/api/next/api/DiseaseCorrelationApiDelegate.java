package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
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
 * A delegate to be called by the {@link DiseaseCorrelationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface DiseaseCorrelationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/disease-correlation : Get disease correlation comparison data
     * Returns a list of disease correlation objects for use in comparison tools.
     *
     * @param category An ordered list of categories used to filter the data, where the first value is the category and the second is the subcategory. Pass each value by repeating the &#39;category&#39; query parameter, e.g. ?category&#x3D;category1&amp;category&#x3D;subcategoryA. (required)
     * @param item A list of items to filter the data by. (optional)
     * @param itemFilterType The type of filter to apply to the items. Possible values are &#39;include&#39; or &#39;exclude&#39;. (optional, default to include)
     * @return A list of disease correlation objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DiseaseCorrelationApi#getDiseaseCorrelations
     */
    default ResponseEntity<List<DiseaseCorrelationDto>> getDiseaseCorrelations(List<String> category,
        List<String> item,
        ItemFilterTypeQueryDto itemFilterType) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"cluster\" : \"cluster\", \"DLPFC\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"sex\" : \"Female\", \"CBE\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"STG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"model_type\" : \"model_type\", \"TCX\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"FP\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"IFG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"matched_control\" : \"matched_control\", \"PHG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"name\" : \"name\", \"_id\" : \"_id\", \"age\" : \"age\" }, { \"cluster\" : \"cluster\", \"DLPFC\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"sex\" : \"Female\", \"CBE\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"STG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"model_type\" : \"model_type\", \"TCX\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"FP\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"IFG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"matched_control\" : \"matched_control\", \"PHG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"name\" : \"name\", \"_id\" : \"_id\", \"age\" : \"age\" } ]";
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
