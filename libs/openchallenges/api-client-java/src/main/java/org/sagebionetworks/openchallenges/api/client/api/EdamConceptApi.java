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
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptsPage;
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
public class EdamConceptApi {

  private ApiClient apiClient;

  public EdamConceptApi() {
    this(new ApiClient());
  }

  public EdamConceptApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * List EDAM concepts
   * List EDAM concepts
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param edamConceptSearchQuery The search query used to find EDAM concepts.
   * @return EdamConceptsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listEdamConceptsRequestCreation(
    @jakarta.annotation.Nullable EdamConceptSearchQuery edamConceptSearchQuery
  ) throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "pageNumber", edamConceptSearchQuery.getPageNumber())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "pageSize", edamConceptSearchQuery.getPageSize())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sort", edamConceptSearchQuery.getSort())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "direction", edamConceptSearchQuery.getDirection())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "ids", edamConceptSearchQuery.getIds())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "searchTerms",
        edamConceptSearchQuery.getSearchTerms()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sections", edamConceptSearchQuery.getSections())
    );

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<EdamConceptsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/edam-concepts",
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
   * List EDAM concepts
   * List EDAM concepts
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param edamConceptSearchQuery The search query used to find EDAM concepts.
   * @return EdamConceptsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public EdamConceptsPage listEdamConcepts(
    @jakarta.annotation.Nullable EdamConceptSearchQuery edamConceptSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<EdamConceptsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listEdamConceptsRequestCreation(edamConceptSearchQuery).body(localVarReturnType);
  }

  /**
   * List EDAM concepts
   * List EDAM concepts
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param edamConceptSearchQuery The search query used to find EDAM concepts.
   * @return ResponseEntity&lt;EdamConceptsPage&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<EdamConceptsPage> listEdamConceptsWithHttpInfo(
    @jakarta.annotation.Nullable EdamConceptSearchQuery edamConceptSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<EdamConceptsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listEdamConceptsRequestCreation(edamConceptSearchQuery).toEntity(localVarReturnType);
  }

  /**
   * List EDAM concepts
   * List EDAM concepts
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param edamConceptSearchQuery The search query used to find EDAM concepts.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listEdamConceptsWithResponseSpec(
    @jakarta.annotation.Nullable EdamConceptSearchQuery edamConceptSearchQuery
  ) throws RestClientResponseException {
    return listEdamConceptsRequestCreation(edamConceptSearchQuery);
  }
}
