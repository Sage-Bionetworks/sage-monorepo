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
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatform;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformCreateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformUpdateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformsPage;
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
public class ChallengePlatformApi {

  private ApiClient apiClient;

  public ChallengePlatformApi() {
    this(new ApiClient());
  }

  public ChallengePlatformApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create a challenge platform
   * Create a challenge platform with the specified ID
   * <p><b>201</b> - Success
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformCreateRequest The challengePlatformCreateRequest parameter
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec createChallengePlatformRequestCreation(
    @jakarta.annotation.Nonnull ChallengePlatformCreateRequest challengePlatformCreateRequest
  ) throws RestClientResponseException {
    Object postBody = challengePlatformCreateRequest;
    // verify the required parameter 'challengePlatformCreateRequest' is set
    if (challengePlatformCreateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengePlatformCreateRequest' when calling createChallengePlatform",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenge-platforms",
      HttpMethod.POST,
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
   * Create a challenge platform
   * Create a challenge platform with the specified ID
   * <p><b>201</b> - Success
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformCreateRequest The challengePlatformCreateRequest parameter
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengePlatform createChallengePlatform(
    @jakarta.annotation.Nonnull ChallengePlatformCreateRequest challengePlatformCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengePlatformRequestCreation(challengePlatformCreateRequest).body(
      localVarReturnType
    );
  }

  /**
   * Create a challenge platform
   * Create a challenge platform with the specified ID
   * <p><b>201</b> - Success
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformCreateRequest The challengePlatformCreateRequest parameter
   * @return ResponseEntity&lt;ChallengePlatform&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengePlatform> createChallengePlatformWithHttpInfo(
    @jakarta.annotation.Nonnull ChallengePlatformCreateRequest challengePlatformCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengePlatformRequestCreation(challengePlatformCreateRequest).toEntity(
      localVarReturnType
    );
  }

  /**
   * Create a challenge platform
   * Create a challenge platform with the specified ID
   * <p><b>201</b> - Success
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformCreateRequest The challengePlatformCreateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec createChallengePlatformWithResponseSpec(
    @jakarta.annotation.Nonnull ChallengePlatformCreateRequest challengePlatformCreateRequest
  ) throws RestClientResponseException {
    return createChallengePlatformRequestCreation(challengePlatformCreateRequest);
  }

  /**
   * Delete a challenge platform
   * Deletes a challenge platform by its unique ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteChallengePlatformRequestCreation(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengePlatformId' is set
    if (challengePlatformId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengePlatformId' when calling deleteChallengePlatform",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengePlatformId", challengePlatformId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenge-platforms/{challengePlatformId}",
      HttpMethod.DELETE,
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
   * Delete a challenge platform
   * Deletes a challenge platform by its unique ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteChallengePlatform(@jakarta.annotation.Nonnull Long challengePlatformId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteChallengePlatformRequestCreation(challengePlatformId).body(localVarReturnType);
  }

  /**
   * Delete a challenge platform
   * Deletes a challenge platform by its unique ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteChallengePlatformWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteChallengePlatformRequestCreation(challengePlatformId).toEntity(localVarReturnType);
  }

  /**
   * Delete a challenge platform
   * Deletes a challenge platform by its unique ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteChallengePlatformWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    return deleteChallengePlatformRequestCreation(challengePlatformId);
  }

  /**
   * Get a challenge platform
   * Returns the challenge platform identified by its unique ID
   * <p><b>200</b> - Success
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getChallengePlatformRequestCreation(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengePlatformId' is set
    if (challengePlatformId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengePlatformId' when calling getChallengePlatform",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengePlatformId", challengePlatformId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenge-platforms/{challengePlatformId}",
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
   * Get a challenge platform
   * Returns the challenge platform identified by its unique ID
   * <p><b>200</b> - Success
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengePlatform getChallengePlatform(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengePlatformRequestCreation(challengePlatformId).body(localVarReturnType);
  }

  /**
   * Get a challenge platform
   * Returns the challenge platform identified by its unique ID
   * <p><b>200</b> - Success
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @return ResponseEntity&lt;ChallengePlatform&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengePlatform> getChallengePlatformWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengePlatformRequestCreation(challengePlatformId).toEntity(localVarReturnType);
  }

  /**
   * Get a challenge platform
   * Returns the challenge platform identified by its unique ID
   * <p><b>200</b> - Success
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getChallengePlatformWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengePlatformId
  ) throws RestClientResponseException {
    return getChallengePlatformRequestCreation(challengePlatformId);
  }

  /**
   * List challenge platforms
   * List challenge platforms
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformSearchQuery The search query used to find challenge platforms.
   * @return ChallengePlatformsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listChallengePlatformsRequestCreation(
    @jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery
  ) throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "pageNumber",
        challengePlatformSearchQuery.getPageNumber()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "pageSize",
        challengePlatformSearchQuery.getPageSize()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sort", challengePlatformSearchQuery.getSort())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "direction",
        challengePlatformSearchQuery.getDirection()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "slugs", challengePlatformSearchQuery.getSlugs())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "searchTerms",
        challengePlatformSearchQuery.getSearchTerms()
      )
    );

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenge-platforms",
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
   * List challenge platforms
   * List challenge platforms
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformSearchQuery The search query used to find challenge platforms.
   * @return ChallengePlatformsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengePlatformsPage listChallengePlatforms(
    @jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengePlatformsRequestCreation(challengePlatformSearchQuery).body(
      localVarReturnType
    );
  }

  /**
   * List challenge platforms
   * List challenge platforms
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformSearchQuery The search query used to find challenge platforms.
   * @return ResponseEntity&lt;ChallengePlatformsPage&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengePlatformsPage> listChallengePlatformsWithHttpInfo(
    @jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengePlatformsRequestCreation(challengePlatformSearchQuery).toEntity(
      localVarReturnType
    );
  }

  /**
   * List challenge platforms
   * List challenge platforms
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformSearchQuery The search query used to find challenge platforms.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listChallengePlatformsWithResponseSpec(
    @jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery
  ) throws RestClientResponseException {
    return listChallengePlatformsRequestCreation(challengePlatformSearchQuery);
  }

  /**
   * Update an existing challenge platform
   * Updates an existing challenge platform.
   * <p><b>200</b> - Challange platform updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @param challengePlatformUpdateRequest The challengePlatformUpdateRequest parameter
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec updateChallengePlatformRequestCreation(
    @jakarta.annotation.Nonnull Long challengePlatformId,
    @jakarta.annotation.Nonnull ChallengePlatformUpdateRequest challengePlatformUpdateRequest
  ) throws RestClientResponseException {
    Object postBody = challengePlatformUpdateRequest;
    // verify the required parameter 'challengePlatformId' is set
    if (challengePlatformId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengePlatformId' when calling updateChallengePlatform",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengePlatformUpdateRequest' is set
    if (challengePlatformUpdateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengePlatformUpdateRequest' when calling updateChallengePlatform",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengePlatformId", challengePlatformId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenge-platforms/{challengePlatformId}",
      HttpMethod.PUT,
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
   * Update an existing challenge platform
   * Updates an existing challenge platform.
   * <p><b>200</b> - Challange platform updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @param challengePlatformUpdateRequest The challengePlatformUpdateRequest parameter
   * @return ChallengePlatform
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengePlatform updateChallengePlatform(
    @jakarta.annotation.Nonnull Long challengePlatformId,
    @jakarta.annotation.Nonnull ChallengePlatformUpdateRequest challengePlatformUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengePlatformRequestCreation(
      challengePlatformId,
      challengePlatformUpdateRequest
    ).body(localVarReturnType);
  }

  /**
   * Update an existing challenge platform
   * Updates an existing challenge platform.
   * <p><b>200</b> - Challange platform updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @param challengePlatformUpdateRequest The challengePlatformUpdateRequest parameter
   * @return ResponseEntity&lt;ChallengePlatform&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengePlatform> updateChallengePlatformWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengePlatformId,
    @jakarta.annotation.Nonnull ChallengePlatformUpdateRequest challengePlatformUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengePlatform> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengePlatformRequestCreation(
      challengePlatformId,
      challengePlatformUpdateRequest
    ).toEntity(localVarReturnType);
  }

  /**
   * Update an existing challenge platform
   * Updates an existing challenge platform.
   * <p><b>200</b> - Challange platform updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengePlatformId The unique identifier of the challenge platform.
   * @param challengePlatformUpdateRequest The challengePlatformUpdateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec updateChallengePlatformWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengePlatformId,
    @jakarta.annotation.Nonnull ChallengePlatformUpdateRequest challengePlatformUpdateRequest
  ) throws RestClientResponseException {
    return updateChallengePlatformRequestCreation(
      challengePlatformId,
      challengePlatformUpdateRequest
    );
  }
}
