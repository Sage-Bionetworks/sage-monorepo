package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfilePageDto;
import org.sagebionetworks.agora.gene.api.model.dto.ProteinDifferentialExpressionProfileSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfilePageDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSearchQueryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link DifferentialExpressionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface DifferentialExpressionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /differentialExpression/protein : List protein differential expression profiles
     * List protein differential expression profiles
     *
     * @param proteinDifferentialExpressionProfileSearchQuery The search query used to find protein differential expression profiles. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DifferentialExpressionApi#listProteinDifferentialExpressionProfiles
     */
    default ResponseEntity<ProteinDifferentialExpressionProfilePageDto> listProteinDifferentialExpressionProfiles(ProteinDifferentialExpressionProfileSearchQueryDto proteinDifferentialExpressionProfileSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"total_elements\" : 99, \"has_previous\" : true, \"has_next\" : true, \"total_pages\" : 99, \"proteinDifferentialExpressionProfiles\" : [ { \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" }, { \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" } ] }";
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
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /differentialExpression/rna : List RNA differential expression profiles
     * List differential expression profiles
     *
     * @param rnaDifferentialExpressionProfileSearchQuery The search query used to find RNA differential expression profiles. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DifferentialExpressionApi#listRnaDifferentialExpressionProfiles
     */
    default ResponseEntity<RnaDifferentialExpressionProfilePageDto> listRnaDifferentialExpressionProfiles(RnaDifferentialExpressionProfileSearchQueryDto rnaDifferentialExpressionProfileSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"total_elements\" : 99, \"has_previous\" : true, \"rnaDifferentialExpressionProfiles\" : [ { \"multi_omics_score\" : 1.4658129805029452, \"tissues\" : [ { \"adj_p_val\" : 5.637377, \"logfc\" : 5.962134, \"ci_r\" : 7.0614014, \"name\" : \"name\", \"ci_l\" : 2.302136, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } }, { \"adj_p_val\" : 5.637377, \"logfc\" : 5.962134, \"ci_r\" : 7.0614014, \"name\" : \"name\", \"ci_l\" : 2.302136, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } } ], \"target_risk_score\" : 0.8008281904610115, \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\", \"genetics_score\" : 6.027456183070403 }, { \"multi_omics_score\" : 1.4658129805029452, \"tissues\" : [ { \"adj_p_val\" : 5.637377, \"logfc\" : 5.962134, \"ci_r\" : 7.0614014, \"name\" : \"name\", \"ci_l\" : 2.302136, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } }, { \"adj_p_val\" : 5.637377, \"logfc\" : 5.962134, \"ci_r\" : 7.0614014, \"name\" : \"name\", \"ci_l\" : 2.302136, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } } ], \"target_risk_score\" : 0.8008281904610115, \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\", \"genetics_score\" : 6.027456183070403 } ], \"has_next\" : true, \"total_pages\" : 99 }";
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
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
