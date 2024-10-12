package org.sagebionetworks.agora.api.api;

import org.sagebionetworks.agora.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.model.dto.BioDomainDto;
import org.sagebionetworks.agora.api.model.dto.BioDomainInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link BioDomainsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface BioDomainsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /biodomains/{ensg} : Retrieve bioDomain for a given ENSG
     * Get bioDomain
     *
     * @param ensg The ENSG (Ensembl Gene ID) for which to retrieve biodomain data. (required)
     * @return Successful retrieval of bio-domains (status code 200)
     *         or ENSG not found (status code 404)
     *         or Internal server error (status code 500)
     * @see BioDomainsApi#getBioDomain
     */
    default ResponseEntity<List<BioDomainDto>> getBioDomain(String ensg) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"pct_linking_terms\" : 1.4658129, \"n_gene_biodomain_terms\" : 6, \"biodomain\" : \"biodomain\", \"go_terms\" : [ \"go_terms\", \"go_terms\" ], \"n_biodomain_terms\" : 0 }, { \"pct_linking_terms\" : 1.4658129, \"n_gene_biodomain_terms\" : 6, \"biodomain\" : \"biodomain\", \"go_terms\" : [ \"go_terms\", \"go_terms\" ], \"n_biodomain_terms\" : 0 } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /biodomains : List BioDomains
     * List BioDomains
     *
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see BioDomainsApi#listBioDomains
     */
    default ResponseEntity<List<BioDomainInfoDto>> listBioDomains() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"name\" : \"name\" }, { \"name\" : \"name\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
