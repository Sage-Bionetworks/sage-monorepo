package org.sagebionetworks.qtl.api.api;

import org.sagebionetworks.qtl.api.model.dto.BasicErrorDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeQueryDto;
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
 * A delegate to be called by the {@link EqtlPhenotypeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface EqtlPhenotypeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /eqtl-phenotype : Get eQTL phenotype data
     * Returns the expression summaries and cell class effect sizes that drive the plots for a single eQTL, identified by its variant, gene and cell type. 
     *
     * @param eqtlPhenotypeQuery The query that identifies a single eQTL. (required)
     * @return Successfully retrieved the eQTL phenotype data (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see EqtlPhenotypeApi#getEqtlPhenotype
     */
    default ResponseEntity<EqtlPhenotypeDto> getEqtlPhenotype(EqtlPhenotypeQueryDto eqtlPhenotypeQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"ensembl_gene_id\" : \"ENSG00000142599\", \"variant\" : \"rs2120461\", \"effect_size_cell_class_data\" : [ { \"cell_class\" : \"Astrocyte\", \"ci_left\" : 0.137115, \"ci_right\" : 0.748307, \"cell_class_short\" : \"Astro\", \"cell_type_id\" : \"class_astro\", \"value\" : 0.442711 }, { \"cell_class\" : \"Astrocyte\", \"ci_left\" : 0.137115, \"ci_right\" : 0.748307, \"cell_class_short\" : \"Astro\", \"cell_type_id\" : \"class_astro\", \"value\" : 0.442711 } ], \"cell_type_id\" : \"astro\", \"expression_summaries\" : [ { \"min\" : -2.2889, \"first_quartile\" : -0.5532, \"median\" : -0.004, \"max\" : 2.3396, \"genotype\" : \"CT\", \"third_quartile\" : 0.6039 }, { \"min\" : -2.2889, \"first_quartile\" : -0.5532, \"median\" : -0.004, \"max\" : 2.3396, \"genotype\" : \"CT\", \"third_quartile\" : 0.6039 } ] }";
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
