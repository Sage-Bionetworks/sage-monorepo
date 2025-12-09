package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
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
     * Returns a paginated list of disease correlation objects for use in comparison tools.
     *
     * @param categories Comma-delimited category values from the dropdown selections. (required)
     * @param sortFields Comma-delimited field names to sort by (e.g., \&quot;name,age,sex\&quot;). (required)
     * @param sortOrders Comma-delimited sort directions corresponding to sortFields. Values 1 (ascending) or -1 (descending). (required)
     * @param pageNumber The page number. (optional, default to 0)
     * @param pageSize The number of items in a single page. (optional, default to 100)
     * @param search  (optional)
     * @param items Comma-delimited list of composite identifiers to filter by. (optional)
     * @param itemFilterType  (optional, default to include)
     * @return A paginated response containing disease correlation objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DiseaseCorrelationApi#getDiseaseCorrelations
     */
    default ResponseEntity<DiseaseCorrelationsPageDto> getDiseaseCorrelations(String categories,
        String sortFields,
        String sortOrders,
        Integer pageNumber,
        Integer pageSize,
        String search,
        String items,
        ItemFilterTypeQueryDto itemFilterType) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"diseaseCorrelations\" : [ { \"cluster\" : \"cluster\", \"DLPFC\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"sex\" : \"Female\", \"CBE\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"STG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"model_type\" : \"model_type\", \"TCX\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"FP\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"IFG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"matched_control\" : \"matched_control\", \"PHG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"composite_id\" : \"APOE4~12 Months~Female\", \"name\" : \"name\", \"age\" : \"age\" }, { \"cluster\" : \"cluster\", \"DLPFC\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"sex\" : \"Female\", \"CBE\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"STG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"model_type\" : \"model_type\", \"TCX\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"modified_genes\" : [ \"modified_genes\", \"modified_genes\" ], \"FP\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"IFG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"matched_control\" : \"matched_control\", \"PHG\" : { \"adj_p_val\" : 6.027456183070403, \"correlation\" : 0.8008281904610115 }, \"composite_id\" : \"APOE4~12 Months~Female\", \"name\" : \"name\", \"age\" : \"age\" } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
