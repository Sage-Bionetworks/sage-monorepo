package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;
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
 * A delegate to be called by the {@link ProteomicsIndividualApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ProteomicsIndividualApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /proteomics-individual : Get proteomics individual data
     * Retrieve proteomics individual data based on specified filter criteria.
     *
     * @param proteomicsIndividualFilterQuery The filter query used to retrieve a specific set of proteomics individual data. (required)
     * @return Successfully retrieved individual proteomics data (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ProteomicsIndividualApi#getProteomicsIndividual
     */
    default ResponseEntity<List<ProteomicsIndividualDto>> getProteomicsIndividual(ProteomicsIndividualFilterQueryDto proteomicsIndividualFilterQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"model_group\" : \"model_group\", \"unique_id\" : \"ENSMUSG00000000001P27144\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"gene_symbol\" : \"Gnai3\", \"display_symbol\" : \"Gnai3 (P27144)\", \"tissue\" : \"Hemibrain\", \"age_numeric\" : 4, \"units\" : \"Log2 Counts per Million\", \"matched_control\" : \"C57BL/6J\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"result_order\" : [ \"C57BL6J\", \"Trem2\" ], \"name\" : \"name\", \"uniprotid\" : \"P27144\", \"age\" : \"4 months\" }, { \"model_group\" : \"model_group\", \"unique_id\" : \"ENSMUSG00000000001P27144\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"gene_symbol\" : \"Gnai3\", \"display_symbol\" : \"Gnai3 (P27144)\", \"tissue\" : \"Hemibrain\", \"age_numeric\" : 4, \"units\" : \"Log2 Counts per Million\", \"matched_control\" : \"C57BL/6J\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"result_order\" : [ \"C57BL6J\", \"Trem2\" ], \"name\" : \"name\", \"uniprotid\" : \"P27144\", \"age\" : \"4 months\" } ]";
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
