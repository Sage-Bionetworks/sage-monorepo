package org.sagebionetworks.openchallenges.api.client.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.api.client.ApiClient;
import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.Image;
import org.sagebionetworks.openchallenges.api.client.model.ImageQuery;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientResponseException;

@jakarta.annotation.Generated(
  value = "org.openapitools.codegen.languages.JavaClientCodegen",
  comments = "Generator version: 7.14.0"
)
public class ImageApi {

  private ApiClient apiClient;

  public ImageApi() {
    this(new ApiClient());
  }

  public ImageApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Get an image
   * Returns the image specified.
   * <p><b>200</b> - An image
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param imageQuery The query used to get an image.
   * @return Image
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getImageRequestCreation(@jakarta.annotation.Nullable ImageQuery imageQuery)
    throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "objectKey", imageQuery.getObjectKey())
    );
    queryParams.putAll(apiClient.parameterToMultiValueMap(null, "height", imageQuery.getHeight()));
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "aspectRatio", imageQuery.getAspectRatio())
    );

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<Image> localVarReturnType = new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/images",
      HttpMethod.GET,
      pathParams,
      queryParams,
      postBody,
      headerParams,
      cookieParams,
      formParams,
      localVarAccept,
      localVarContentType,
      localVarAuthNames,
      localVarReturnType
    );
  }

  /**
   * Get an image
   * Returns the image specified.
   * <p><b>200</b> - An image
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param imageQuery The query used to get an image.
   * @return Image
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public Image getImage(@jakarta.annotation.Nullable ImageQuery imageQuery)
    throws RestClientResponseException {
    ParameterizedTypeReference<Image> localVarReturnType = new ParameterizedTypeReference<>() {};
    return getImageRequestCreation(imageQuery).body(localVarReturnType);
  }

  /**
   * Get an image
   * Returns the image specified.
   * <p><b>200</b> - An image
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param imageQuery The query used to get an image.
   * @return ResponseEntity&lt;Image&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Image> getImageWithHttpInfo(
    @jakarta.annotation.Nullable ImageQuery imageQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Image> localVarReturnType = new ParameterizedTypeReference<>() {};
    return getImageRequestCreation(imageQuery).toEntity(localVarReturnType);
  }

  /**
   * Get an image
   * Returns the image specified.
   * <p><b>200</b> - An image
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param imageQuery The query used to get an image.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getImageWithResponseSpec(@jakarta.annotation.Nullable ImageQuery imageQuery)
    throws RestClientResponseException {
    return getImageRequestCreation(imageQuery);
  }
}
