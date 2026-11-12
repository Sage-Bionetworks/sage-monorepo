package org.sagebionetworks.qtl.api.api;

import org.sagebionetworks.qtl.api.model.dto.BasicErrorDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
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
 * A delegate to be called by the {@link EqtlApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface EqtlApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /comparison-tools/eqtl : Get eQTL comparison data
     * Returns a paginated list of eQTL objects for use in comparison tools.
     *
     * @param eqtlSearchQuery The search query used to find and filter eQTLs. (optional)
     * @return A paginated response containing eQTL objects (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see EqtlApi#getEqtls
     */
    default ResponseEntity<EqtlsPageDto> getEqtls(EqtlSearchQueryDto eqtlSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"eqtls\" : [ { \"gene_symbol\" : \"LINC02208\", \"adj_p_val_numeric\" : -161.583901, \"minor_allele\" : \"C\", \"cell_subclass\" : \"Astrocyte\", \"effect_direction\" : \"Up\", \"adj_p_val\" : \"2.61E-162\", \"composite_id\" : \"rs12655812~ENSG00000250891~astro\", \"maf\" : 0.1418, \"reference_allele\" : \"T\", \"cell_class\" : \"Astrocyte\", \"cell_subclass_short\" : \"Astro\", \"ensembl_gene_id\" : \"ENSG00000250891\", \"cell_class_short\" : \"Astro\", \"variant\" : \"rs12655812\", \"cell_type_id\" : \"astro\", \"variant_location\" : { \"start\" : 118488207, \"end\" : 118488208, \"chr\" : \"5\" }, \"gene_location\" : { \"start\" : 118488207, \"end\" : 118488208, \"chr\" : \"5\" }, \"beta\" : 0.608491 }, { \"gene_symbol\" : \"LINC02208\", \"adj_p_val_numeric\" : -161.583901, \"minor_allele\" : \"C\", \"cell_subclass\" : \"Astrocyte\", \"effect_direction\" : \"Up\", \"adj_p_val\" : \"2.61E-162\", \"composite_id\" : \"rs12655812~ENSG00000250891~astro\", \"maf\" : 0.1418, \"reference_allele\" : \"T\", \"cell_class\" : \"Astrocyte\", \"cell_subclass_short\" : \"Astro\", \"ensembl_gene_id\" : \"ENSG00000250891\", \"cell_class_short\" : \"Astro\", \"variant\" : \"rs12655812\", \"cell_type_id\" : \"astro\", \"variant_location\" : { \"start\" : 118488207, \"end\" : 118488208, \"chr\" : \"5\" }, \"gene_location\" : { \"start\" : 118488207, \"end\" : 118488208, \"chr\" : \"5\" }, \"beta\" : 0.608491 } ], \"page\" : { \"number\" : 0, \"size\" : 100, \"totalPages\" : 3, \"hasPrevious\" : false, \"hasNext\" : true, \"totalElements\" : 250 } }";
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
