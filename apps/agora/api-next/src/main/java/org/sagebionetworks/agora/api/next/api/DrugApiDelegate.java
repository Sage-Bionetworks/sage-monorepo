package org.sagebionetworks.agora.api.next.api;

import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
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
 * A delegate to be called by the {@link DrugApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface DrugApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /drugs/{chembl_id} : Get drug details by ChEMBL ID
     *
     * @param chemblId ChEMBL ID of the drug (required)
     * @return Drug details successfully retrieved (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see DrugApi#getDrug
     */
    default ResponseEntity<DrugDto> getDrug(String chemblId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"drug_bank_id\" : \"DB04819\", \"maximum_clinical_trial_phase\" : \"Phase IV\", \"year_of_first_approval\" : 2010, \"linked_targets\" : { \"ensembl_gene_id\" : \"ENSG00000139618\", \"hgnc_symbol\" : \"BRCA1\" }, \"aliases\" : [ \"Agomelatina\", \"S-20098\", \"Valdoxan\" ], \"modality\" : \"Protein\", \"drug_nominations\" : [ { \"computational_validation_status\" : \"computational validation studies completed\", \"evidence\" : \"Preclinical studies showing efficacy in AD mouse models\", \"data_used\" : \"transcriptomics data, drug target\", \"combined_with_common_name\" : \"combined_with_common_name\", \"experimental_validation_results\" : \"improve cognitive, reduce neuroinflamation in female Tg344 rat AD models.\", \"program\" : \"ACTDRx AD\", \"contact_pi\" : \"Dr. Jane Doe\", \"computational_validation_results\" : \"Based on putative targets associated with AD\", \"reference\" : \"https://doi.org/10.1101/2023.12.26.573348\", \"additional_evidence\" : \"may penetrate blood-brain barrier\", \"grant_number\" : \"R01AG123456\", \"experimental_validation_status\" : \"experimental validation studies completed\", \"initial_nomination\" : 2025, \"ad_moa\" : \"DDIT3\", \"contributors\" : \"Kelechi Ndukwe, Peter A Serrano, Patricia Rockwell, Lei Xie, Maria Figueiredo-Pereira\", \"combined_with_chembl_id\" : \"combined_with_chembl_id\" }, { \"computational_validation_status\" : \"computational validation studies completed\", \"evidence\" : \"Preclinical studies showing efficacy in AD mouse models\", \"data_used\" : \"transcriptomics data, drug target\", \"combined_with_common_name\" : \"combined_with_common_name\", \"experimental_validation_results\" : \"improve cognitive, reduce neuroinflamation in female Tg344 rat AD models.\", \"program\" : \"ACTDRx AD\", \"contact_pi\" : \"Dr. Jane Doe\", \"computational_validation_results\" : \"Based on putative targets associated with AD\", \"reference\" : \"https://doi.org/10.1101/2023.12.26.573348\", \"additional_evidence\" : \"may penetrate blood-brain barrier\", \"grant_number\" : \"R01AG123456\", \"experimental_validation_status\" : \"experimental validation studies completed\", \"initial_nomination\" : 2025, \"ad_moa\" : \"DDIT3\", \"contributors\" : \"Kelechi Ndukwe, Peter A Serrano, Patricia Rockwell, Lei Xie, Maria Figueiredo-Pereira\", \"combined_with_chembl_id\" : \"combined_with_chembl_id\" } ], \"mechanisms_of_action\" : [ \"Melatonin receptor agonist\", \"Serotonin 2c (5-HT2c) receptor antagonist\" ], \"description\" : \"description\", \"common_name\" : \"Agomelatine\", \"iupac_id\" : \"iupac_id\", \"chembl_id\" : \"CHEMBL2105758\" }";
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
