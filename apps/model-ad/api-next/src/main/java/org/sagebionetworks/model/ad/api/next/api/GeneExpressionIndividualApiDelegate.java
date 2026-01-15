package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualFilterQueryDto;
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
 * A delegate to be called by the {@link GeneExpressionIndividualApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface GeneExpressionIndividualApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /gene-expression-individual : Get gene expression individual data
     * Retrieve gene expression individual data based on specified filter criteria.
     *
     * @param geneExpressionIndividualFilterQuery The filter query used to retrieve a specific set of gene expression individual data. (optional)
     * @return Successfully retrieved individual gene expression data (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see GeneExpressionIndividualApi#getGeneExpressionIndividual
     */
    default ResponseEntity<List<GeneExpressionIndividualDto>> getGeneExpressionIndividual(GeneExpressionIndividualFilterQueryDto geneExpressionIndividualFilterQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"model_group\" : \"model_group\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"ensembl_gene_id\" : \"ensembl_gene_id\", \"result_order\" : [ \"C57BL6J\", \"Trem2\" ], \"gene_symbol\" : \"Gnai3\", \"name\" : \"name\", \"tissue\" : \"Hemibrain\", \"age_numeric\" : 4, \"units\" : \"Log2 Counts per Million\", \"matched_control\" : \"C57BL/6J\", \"age\" : \"4 months\" }, { \"model_group\" : \"model_group\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 0.8008281904610115, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"ensembl_gene_id\" : \"ensembl_gene_id\", \"result_order\" : [ \"C57BL6J\", \"Trem2\" ], \"gene_symbol\" : \"Gnai3\", \"name\" : \"name\", \"tissue\" : \"Hemibrain\", \"age_numeric\" : 4, \"units\" : \"Log2 Counts per Million\", \"matched_control\" : \"C57BL/6J\", \"age\" : \"4 months\" } ]";
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
