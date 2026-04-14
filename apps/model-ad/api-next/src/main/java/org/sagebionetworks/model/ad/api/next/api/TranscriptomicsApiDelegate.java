package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
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
 * A delegate to be called by the {@link TranscriptomicsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface TranscriptomicsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/transcriptomics : Get transcriptomics comparison data
     * Returns a paginated list of transcriptomics objects for use in comparison tools.
     *
     * @param transcriptomicsSearchQuery The search query used to find and filter transcriptomics data. (optional)
     * @return A paginated response containing transcriptomics objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see TranscriptomicsApi#getTranscriptomics
     */
    default ResponseEntity<TranscriptomicsPageDto> getTranscriptomics(TranscriptomicsSearchQueryDto transcriptomicsSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"transcriptomics\" : [ { \"model_group\" : \"model_group\", \"gene_symbol\" : \"Gnai3\", \"model_type\" : \"Familial AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"sex_cohort\" : \"Females\", \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] }, { \"model_group\" : \"model_group\", \"gene_symbol\" : \"Gnai3\", \"model_type\" : \"Familial AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"sex_cohort\" : \"Females\", \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
