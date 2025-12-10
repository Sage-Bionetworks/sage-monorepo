package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
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
 * A delegate to be called by the {@link ModelApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface ModelApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /models/{name} : Get details for a specific model
     * Retrieve detailed information for a specific model by its name
     *
     * @param name Name of the model to retrieve (required)
     * @return Successfully retrieved model details (status code 200)
     *         or Invalid request (status code 400)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ModelApi#getModelByName
     */
    default ResponseEntity<ModelDto> getModelByName(String name) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"genetic_info\" : [ { \"modified_gene\" : \"modified_gene\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"allele\" : \"allele\", \"mgi_allele_id\" : 6.027456183070403, \"allele_type\" : \"allele_type\" }, { \"modified_gene\" : \"modified_gene\", \"ensembl_gene_id\" : \"ensembl_gene_id\", \"allele\" : \"allele\", \"mgi_allele_id\" : 6.027456183070403, \"allele_type\" : \"allele_type\" } ], \"aliases\" : [ \"aliases\", \"aliases\" ], \"pathology\" : [ { \"evidence_type\" : \"evidence_type\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"name\" : \"name\", \"tissue\" : \"tissue\", \"units\" : \"units\", \"y_axis_max\" : 1.4658129805029452, \"age\" : \"age\" }, { \"evidence_type\" : \"evidence_type\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"name\" : \"name\", \"tissue\" : \"tissue\", \"units\" : \"units\", \"y_axis_max\" : 1.4658129805029452, \"age\" : \"age\" } ], \"contributing_group\" : \"contributing_group\", \"biomarkers\" : [ { \"evidence_type\" : \"evidence_type\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"name\" : \"name\", \"tissue\" : \"tissue\", \"units\" : \"units\", \"y_axis_max\" : 1.4658129805029452, \"age\" : \"age\" }, { \"evidence_type\" : \"evidence_type\", \"data\" : [ { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" }, { \"sex\" : \"Female\", \"value\" : 5.962133916683182, \"genotype\" : \"genotype\", \"individual_id\" : \"individual_id\" } ], \"name\" : \"name\", \"tissue\" : \"tissue\", \"units\" : \"units\", \"y_axis_max\" : 1.4658129805029452, \"age\" : \"age\" } ], \"gene_expression\" : \"gene_expression\", \"disease_correlation\" : \"disease_correlation\", \"matched_controls\" : [ \"matched_controls\", \"matched_controls\" ], \"model_type\" : \"model_type\", \"jax_id\" : 0.8008281904610115, \"rrid\" : \"rrid\", \"genotype\" : \"genotype\", \"spatial_transcriptomics\" : \"spatial_transcriptomics\", \"alzforum_id\" : \"alzforum_id\", \"study_synid\" : \"study_synid\", \"name\" : \"name\" }";
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
