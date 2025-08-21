package org.sagebionetworks.agora.api.api;

import org.sagebionetworks.agora.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.model.dto.GCTGenesListDto;
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
 * A delegate to be called by the {@link GeneNextApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface GeneNextApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /next/genes/comparison : Get comparison genes based on category and subcategory
     * Get comparison genes based on category and subcategory
     *
     * @param category The category of the comparison (either RNA or Protein Differential Expression). (required)
     * @param subCategory The subcategory for gene comparison (sub-category must be a string). (required)
     * @return Successful response with comparison genes (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see GeneNextApi#getComparisonGenesNext
     */
    default ResponseEntity<GCTGenesListDto> getComparisonGenesNext(String category,
        String subCategory) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"items\" : [ { \"associations\" : [ 1.2315135367772556, 1.2315135367772556 ], \"pinned\" : true, \"tissues\" : [ { \"adj_p_val\" : 6.0274563, \"logfc\" : 0.8008282, \"ci_r\" : 5.962134, \"name\" : \"name\", \"ci_l\" : 1.4658129, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } }, { \"adj_p_val\" : 6.0274563, \"logfc\" : 0.8008282, \"ci_r\" : 5.962134, \"name\" : \"name\", \"ci_l\" : 1.4658129, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } } ], \"search_string\" : \"search_string\", \"nominations\" : { \"teams\" : [ \"teams\", \"teams\" ], \"year\" : 7, \"inputs\" : [ \"inputs\", \"inputs\" ], \"count\" : 4, \"studies\" : [ \"studies\", \"studies\" ], \"programs\" : [ \"programs\", \"programs\" ], \"validations\" : [ \"validations\", \"validations\" ] }, \"multi_omics_score\" : 6.84685269835264, \"uid\" : \"uid\", \"target_risk_score\" : 1.0246457001441578, \"ensembl_gene_id\" : \"ensembl_gene_id\", \"search_array\" : [ \"search_array\", \"search_array\" ], \"hgnc_symbol\" : \"hgnc_symbol\", \"genetics_score\" : 1.4894159098541704, \"uniprotid\" : \"uniprotid\", \"biodomains\" : [ \"biodomains\", \"biodomains\" ], \"target_enabling_resources\" : [ \"target_enabling_resources\", \"target_enabling_resources\" ] }, { \"associations\" : [ 1.2315135367772556, 1.2315135367772556 ], \"pinned\" : true, \"tissues\" : [ { \"adj_p_val\" : 6.0274563, \"logfc\" : 0.8008282, \"ci_r\" : 5.962134, \"name\" : \"name\", \"ci_l\" : 1.4658129, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } }, { \"adj_p_val\" : 6.0274563, \"logfc\" : 0.8008282, \"ci_r\" : 5.962134, \"name\" : \"name\", \"ci_l\" : 1.4658129, \"medianexpression\" : { \"min\" : 5.637377, \"first_quartile\" : 2.302136, \"median\" : 7.0614014, \"max\" : 2.027123, \"mean\" : 9.301444, \"tissue\" : \"tissue\", \"third_quartile\" : 3.6160767 } } ], \"search_string\" : \"search_string\", \"nominations\" : { \"teams\" : [ \"teams\", \"teams\" ], \"year\" : 7, \"inputs\" : [ \"inputs\", \"inputs\" ], \"count\" : 4, \"studies\" : [ \"studies\", \"studies\" ], \"programs\" : [ \"programs\", \"programs\" ], \"validations\" : [ \"validations\", \"validations\" ] }, \"multi_omics_score\" : 6.84685269835264, \"uid\" : \"uid\", \"target_risk_score\" : 1.0246457001441578, \"ensembl_gene_id\" : \"ensembl_gene_id\", \"search_array\" : [ \"search_array\", \"search_array\" ], \"hgnc_symbol\" : \"hgnc_symbol\", \"genetics_score\" : 1.4894159098541704, \"uniprotid\" : \"uniprotid\", \"biodomains\" : [ \"biodomains\", \"biodomains\" ], \"target_enabling_resources\" : [ \"target_enabling_resources\", \"target_enabling_resources\" ] } ] }";
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
