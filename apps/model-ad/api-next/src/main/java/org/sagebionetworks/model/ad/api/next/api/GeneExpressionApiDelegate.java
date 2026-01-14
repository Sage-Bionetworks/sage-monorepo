package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDetailDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDetailFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
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
 * A delegate to be called by the {@link GeneExpressionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface GeneExpressionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /gene-expression-detail : Get gene expression details data
     * Retrieve gene expression details data based on specified filter criteria.
     *
     * @param geneExpressionDetailFilterQuery The filter query used to retrieve a specific set of gene expression detail data. (optional)
     * @return Successfully retrieved individual gene expression data (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see GeneExpressionApi#getGeneExpressionDetails
     */
    default ResponseEntity<List<GeneExpressionDetailDto>> getGeneExpressionDetails(GeneExpressionDetailFilterQueryDto geneExpressionDetailFilterQuery) {
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

    /**
     * GET /comparison-tools/gene-expression : Get gene expression comparison data
     * Returns a paginated list of gene expression objects for use in comparison tools.
     *
     * @param geneExpressionSearchQuery The search query used to find and filter gene expressions. (optional)
     * @return A paginated response containing gene expression objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see GeneExpressionApi#getGeneExpressions
     */
    default ResponseEntity<GeneExpressionsPageDto> getGeneExpressions(GeneExpressionSearchQueryDto geneExpressionSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 }, \"geneExpressions\" : [ { \"model_group\" : \"model_group\", \"gene_symbol\" : \"Gnai3\", \"model_type\" : \"Familial AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"sex_cohort\" : \"Females\", \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] }, { \"model_group\" : \"model_group\", \"gene_symbol\" : \"Gnai3\", \"model_type\" : \"Familial AD\", \"tissue\" : \"Hemibrain\", \"4 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"matched_control\" : \"C57BL/6J\", \"composite_id\" : \"ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"name\" : { \"link_url\" : \"link_url\", \"link_text\" : \"link_text\" }, \"sex_cohort\" : \"Females\", \"12 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"18 months\" : { \"log2_fc\" : 0.8008281904610115, \"adj_p_val\" : 6.027456183070403 }, \"biodomains\" : [ \"biodomains\", \"biodomains\" ] } ] }";
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
