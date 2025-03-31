package org.sagebionetworks.amp.als.dataset.service.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.amp.als.dataset.service.model.dto.BasicErrorDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetJsonLdDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetSearchQueryDto;
import org.sagebionetworks.amp.als.dataset.service.model.dto.DatasetsPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * A delegate to be called by the {@link DatasetApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface DatasetApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /datasets/{datasetId} : Get a dataset
   * Returns the dataset specified
   *
   * @param datasetId The unique identifier of the dataset. (required)
   * @return A dataset (status code 200)
   *         or The specified resource was not found (status code 404)
   *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see DatasetApi#getDataset
   */
  default ResponseEntity<DatasetDto> getDataset(Long datasetId) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString =
              "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"name\" : \"name\", \"description\" : \"This is an example description of the dataset.\", \"id\" : 1, \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
            ApiUtil.setExampleResponse(request, "application/json", exampleString);
            break;
          }
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/ld+json"))) {
            String exampleString =
              "Custom MIME type example not yet supported: application/ld+json";
            ApiUtil.setExampleResponse(request, "application/ld+json", exampleString);
            break;
          }
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
            String exampleString =
              "Custom MIME type example not yet supported: application/problem+json";
            ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
            break;
          }
        }
      });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /datasets : List datasets
   * List datasets
   *
   * @param datasetSearchQuery The search query used to find datasets. (optional)
   * @return Success (status code 200)
   *         or Invalid request (status code 400)
   *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see DatasetApi#listDatasets
   */
  default ResponseEntity<DatasetsPageDto> listDatasets(DatasetSearchQueryDto datasetSearchQuery) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString = "null";
            ApiUtil.setExampleResponse(request, "application/json", exampleString);
            break;
          }
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
            String exampleString =
              "Custom MIME type example not yet supported: application/problem+json";
            ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
            break;
          }
        }
      });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
