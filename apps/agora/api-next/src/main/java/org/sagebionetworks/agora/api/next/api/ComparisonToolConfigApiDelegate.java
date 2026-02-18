package org.sagebionetworks.agora.api.next.api;

import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.agora.api.next.model.dto.ComparisonToolConfigPageDto;
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
 * A delegate to be called by the {@link ComparisonToolConfigApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ComparisonToolConfigApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/config : Get Comparison Tool configuration
     * Retrieve the Comparison Tool configuration schema for the Agora application
     *
     * @param page Name of the page to retrieve the Comparison Tool configuration for (required)
     * @return Successfully retrieved Comparison Tool configuration (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ComparisonToolConfigApi#getComparisonToolsConfig
     */
    default ResponseEntity<List<ComparisonToolConfigDto>> getComparisonToolsConfig(ComparisonToolConfigPageDto page) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"columns\" : [ { \"name\" : \"name\", \"tooltip\" : \"tooltip\", \"is_hidden\" : true, \"link_url\" : \"link_url\", \"sort_tooltip\" : \"sort_tooltip\", \"is_exported\" : true, \"type\" : \"text\", \"data_key\" : \"data_key\", \"link_text\" : \"link_text\" }, { \"name\" : \"name\", \"tooltip\" : \"tooltip\", \"is_hidden\" : true, \"link_url\" : \"link_url\", \"sort_tooltip\" : \"sort_tooltip\", \"is_exported\" : true, \"type\" : \"text\", \"data_key\" : \"data_key\", \"link_text\" : \"link_text\" } ], \"page\" : \"Nominated Targets\", \"dropdowns\" : [ \"RNA - DIFFERENTIAL EXPRESSION\", \"RNA - DIFFERENTIAL EXPRESSION\" ], \"filters\" : [ { \"values\" : [ \"Apoptosis\", \"Apoptosis\" ], \"name\" : \"Biological Domain\", \"short_name\" : \"Biodomain\", \"data_key\" : \"biodomains\", \"query_param_key\" : \"biodomain\" }, { \"values\" : [ \"Apoptosis\", \"Apoptosis\" ], \"name\" : \"Biological Domain\", \"short_name\" : \"Biodomain\", \"data_key\" : \"biodomains\", \"query_param_key\" : \"biodomain\" } ], \"row_count\" : \"row_count\" }, { \"columns\" : [ { \"name\" : \"name\", \"tooltip\" : \"tooltip\", \"is_hidden\" : true, \"link_url\" : \"link_url\", \"sort_tooltip\" : \"sort_tooltip\", \"is_exported\" : true, \"type\" : \"text\", \"data_key\" : \"data_key\", \"link_text\" : \"link_text\" }, { \"name\" : \"name\", \"tooltip\" : \"tooltip\", \"is_hidden\" : true, \"link_url\" : \"link_url\", \"sort_tooltip\" : \"sort_tooltip\", \"is_exported\" : true, \"type\" : \"text\", \"data_key\" : \"data_key\", \"link_text\" : \"link_text\" } ], \"page\" : \"Nominated Targets\", \"dropdowns\" : [ \"RNA - DIFFERENTIAL EXPRESSION\", \"RNA - DIFFERENTIAL EXPRESSION\" ], \"filters\" : [ { \"values\" : [ \"Apoptosis\", \"Apoptosis\" ], \"name\" : \"Biological Domain\", \"short_name\" : \"Biodomain\", \"data_key\" : \"biodomains\", \"query_param_key\" : \"biodomain\" }, { \"values\" : [ \"Apoptosis\", \"Apoptosis\" ], \"name\" : \"Biological Domain\", \"short_name\" : \"Biodomain\", \"data_key\" : \"biodomains\", \"query_param_key\" : \"biodomain\" } ], \"row_count\" : \"row_count\" } ]";
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
