package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileProteinSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesProteinPageDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesRnaPageDto;
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
 * A delegate to be called by the {@link DifferentialExpressionProfileApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface DifferentialExpressionProfileApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /differentialExpressionProfiles/protein : List differential expression profiles (protein)
     * List differential expression profiles (protein)
     *
     * @param differentialExpressionProfileProteinSearchQuery The search query used to find differential expression profiles (protein). (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DifferentialExpressionProfileApi#listDifferentialExpressionProfilesProtein
     */
    default ResponseEntity<DifferentialExpressionProfilesProteinPageDto> listDifferentialExpressionProfilesProtein(DifferentialExpressionProfileProteinSearchQueryDto differentialExpressionProfileProteinSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"differentialExpressionProfilesProtein\" : [ { \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" }, { \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" } ], \"size\" : 99, \"total_elements\" : 99, \"has_previous\" : true, \"has_next\" : true, \"total_pages\" : 99 }";
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
     * GET /differentialExpressionProfiles/rna : List differential expression profiles (RNA)
     * List differential expression profiles (RNA)
     *
     * @param differentialExpressionProfileRnaSearchQuery The search query used to find differential expression profiles (RNA). (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DifferentialExpressionProfileApi#listDifferentialExpressionProfilesRna
     */
    default ResponseEntity<DifferentialExpressionProfilesRnaPageDto> listDifferentialExpressionProfilesRna(DifferentialExpressionProfileRnaSearchQueryDto differentialExpressionProfileRnaSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"total_elements\" : 99, \"has_previous\" : true, \"differentialExpressionProfilesRna\" : [ { \"target_risk_score\" : 0.8008281904610115, \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" }, { \"target_risk_score\" : 0.8008281904610115, \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"TP53\" } ], \"has_next\" : true, \"total_pages\" : 99 }";
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
