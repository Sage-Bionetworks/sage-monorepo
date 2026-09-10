package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
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
 * A delegate to be called by the {@link ProteomicsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ProteomicsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/proteomics : Get proteomics comparison data
     * Returns a paginated list of proteomics objects for use in comparison tools.
     *
     * @param proteomicsSearchQuery The search query used to find and filter proteomics data. (optional)
     * @return A paginated response containing proteomics objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ProteomicsApi#getProteomics
     */
    default ResponseEntity<ProteomicsPageDto> getProteomics(ProteomicsSearchQueryDto proteomicsSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"proteomics\" : [ { \"model_group\" : \"model_group\", \"unique_id\" : \"ENSMUSG00000000001P27144\", \"24 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"gene_symbol\" : \"Gnai3\", \"sex\" : \"Female\", \"display_symbol\" : \"Gnai3 (P27144)\", \"model_type\" : \"Late Onset AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001P27144~LOAD2~Female\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"uniprotid\" : \"P27144\", \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] }, { \"model_group\" : \"model_group\", \"unique_id\" : \"ENSMUSG00000000001P27144\", \"24 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"gene_symbol\" : \"Gnai3\", \"sex\" : \"Female\", \"display_symbol\" : \"Gnai3 (P27144)\", \"model_type\" : \"Late Onset AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001P27144~LOAD2~Female\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"uniprotid\" : \"P27144\", \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
