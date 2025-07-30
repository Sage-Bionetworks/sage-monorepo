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
import org.sagebionetworks.openchallenges.api.client.model.Challenge;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeCreateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeJsonLd;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeUpdateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPage;
import org.springframework.beans.factory.annotation.Autowired;
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
  comments = "Generator version: 7.13.0"
)
public class ChallengeApi {

  private ApiClient apiClient;

  public ChallengeApi() {
    this(new ApiClient());
  }

  @Autowired
  public ChallengeApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create a challenge
   * Create a challenge with the specified details
   * <p><b>201</b> - Challenge created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeCreateRequest The challengeCreateRequest parameter
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec createChallengeRequestCreation(
    @jakarta.annotation.Nonnull ChallengeCreateRequest challengeCreateRequest
  ) throws RestClientResponseException {
    Object postBody = challengeCreateRequest;
    // verify the required parameter 'challengeCreateRequest' is set
    if (challengeCreateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeCreateRequest' when calling createChallenge",
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

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges",
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
   * Create a challenge
   * Create a challenge with the specified details
   * <p><b>201</b> - Challenge created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeCreateRequest The challengeCreateRequest parameter
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public Challenge createChallenge(
    @jakarta.annotation.Nonnull ChallengeCreateRequest challengeCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeRequestCreation(challengeCreateRequest).body(localVarReturnType);
  }

  /**
   * Create a challenge
   * Create a challenge with the specified details
   * <p><b>201</b> - Challenge created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeCreateRequest The challengeCreateRequest parameter
   * @return ResponseEntity&lt;Challenge&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Challenge> createChallengeWithHttpInfo(
    @jakarta.annotation.Nonnull ChallengeCreateRequest challengeCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeRequestCreation(challengeCreateRequest).toEntity(localVarReturnType);
  }

  /**
   * Create a challenge
   * Create a challenge with the specified details
   * <p><b>201</b> - Challenge created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeCreateRequest The challengeCreateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec createChallengeWithResponseSpec(
    @jakarta.annotation.Nonnull ChallengeCreateRequest challengeCreateRequest
  ) throws RestClientResponseException {
    return createChallengeRequestCreation(challengeCreateRequest);
  }

  /**
   * Delete a challenge
   * Deletes a challenge by its unique ID.
   * <p><b>204</b> - Challenge successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteChallengeRequestCreation(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling deleteChallenge",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengeId", challengeId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}",
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
   * Delete a challenge
   * Deletes a challenge by its unique ID.
   * <p><b>204</b> - Challenge successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteChallenge(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteChallengeRequestCreation(challengeId).body(localVarReturnType);
  }

  /**
   * Delete a challenge
   * Deletes a challenge by its unique ID.
   * <p><b>204</b> - Challenge successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteChallengeWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteChallengeRequestCreation(challengeId).toEntity(localVarReturnType);
  }

  /**
   * Delete a challenge
   * Deletes a challenge by its unique ID.
   * <p><b>204</b> - Challenge successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteChallengeWithResponseSpec(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    return deleteChallengeRequestCreation(challengeId);
  }

  /**
   * Get a challenge
   * Returns the challenge specified
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getChallengeRequestCreation(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling getChallenge",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengeId", challengeId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}",
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
   * Get a challenge
   * Returns the challenge specified
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public Challenge getChallenge(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeRequestCreation(challengeId).body(localVarReturnType);
  }

  /**
   * Get a challenge
   * Returns the challenge specified
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseEntity&lt;Challenge&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Challenge> getChallengeWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeRequestCreation(challengeId).toEntity(localVarReturnType);
  }

  /**
   * Get a challenge
   * Returns the challenge specified
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getChallengeWithResponseSpec(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    return getChallengeRequestCreation(challengeId);
  }

  /**
   * Get a challenge in JSON-LD format
   * Returns the challenge specified in JSON-LD format
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ChallengeJsonLd
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getChallengeJsonLdRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling getChallengeJsonLd",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengeId", challengeId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/ld+json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<ChallengeJsonLd> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}/json-ld",
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
   * Get a challenge in JSON-LD format
   * Returns the challenge specified in JSON-LD format
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ChallengeJsonLd
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeJsonLd getChallengeJsonLd(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeJsonLd> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeJsonLdRequestCreation(challengeId).body(localVarReturnType);
  }

  /**
   * Get a challenge in JSON-LD format
   * Returns the challenge specified in JSON-LD format
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseEntity&lt;ChallengeJsonLd&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeJsonLd> getChallengeJsonLdWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeJsonLd> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeJsonLdRequestCreation(challengeId).toEntity(localVarReturnType);
  }

  /**
   * Get a challenge in JSON-LD format
   * Returns the challenge specified in JSON-LD format
   * <p><b>200</b> - A challenge
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getChallengeJsonLdWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    return getChallengeJsonLdRequestCreation(challengeId);
  }

  /**
   * List challenges
   * List challenges
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeSearchQuery The search query used to find challenges.
   * @return ChallengesPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listChallengesRequestCreation(
    @jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery
  ) throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "pageNumber", challengeSearchQuery.getPageNumber())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "pageSize", challengeSearchQuery.getPageSize())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sort", challengeSearchQuery.getSort())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sortSeed", challengeSearchQuery.getSortSeed())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "direction", challengeSearchQuery.getDirection())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "incentives", challengeSearchQuery.getIncentives())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "minStartDate",
        challengeSearchQuery.getMinStartDate()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "maxStartDate",
        challengeSearchQuery.getMaxStartDate()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "platforms", challengeSearchQuery.getPlatforms())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "organizations",
        challengeSearchQuery.getOrganizations()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "status", challengeSearchQuery.getStatus())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "submissionTypes",
        challengeSearchQuery.getSubmissionTypes()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "inputDataTypes",
        challengeSearchQuery.getInputDataTypes()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "operations", challengeSearchQuery.getOperations())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "categories", challengeSearchQuery.getCategories())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "searchTerms", challengeSearchQuery.getSearchTerms())
    );

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<ChallengesPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges",
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
   * List challenges
   * List challenges
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeSearchQuery The search query used to find challenges.
   * @return ChallengesPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengesPage listChallenges(
    @jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengesPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengesRequestCreation(challengeSearchQuery).body(localVarReturnType);
  }

  /**
   * List challenges
   * List challenges
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeSearchQuery The search query used to find challenges.
   * @return ResponseEntity&lt;ChallengesPage&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengesPage> listChallengesWithHttpInfo(
    @jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengesPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengesRequestCreation(challengeSearchQuery).toEntity(localVarReturnType);
  }

  /**
   * List challenges
   * List challenges
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeSearchQuery The search query used to find challenges.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listChallengesWithResponseSpec(
    @jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery
  ) throws RestClientResponseException {
    return listChallengesRequestCreation(challengeSearchQuery);
  }

  /**
   * Update an existing challenge
   * Updates an existing challenge.
   * <p><b>200</b> - Challenge successfully updated
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeUpdateRequest The challengeUpdateRequest parameter
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec updateChallengeRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeUpdateRequest challengeUpdateRequest
  ) throws RestClientResponseException {
    Object postBody = challengeUpdateRequest;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling updateChallenge",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeUpdateRequest' is set
    if (challengeUpdateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeUpdateRequest' when calling updateChallenge",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("challengeId", challengeId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}",
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
   * Update an existing challenge
   * Updates an existing challenge.
   * <p><b>200</b> - Challenge successfully updated
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeUpdateRequest The challengeUpdateRequest parameter
   * @return Challenge
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public Challenge updateChallenge(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeUpdateRequest challengeUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengeRequestCreation(challengeId, challengeUpdateRequest).body(
      localVarReturnType
    );
  }

  /**
   * Update an existing challenge
   * Updates an existing challenge.
   * <p><b>200</b> - Challenge successfully updated
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeUpdateRequest The challengeUpdateRequest parameter
   * @return ResponseEntity&lt;Challenge&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Challenge> updateChallengeWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeUpdateRequest challengeUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Challenge> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengeRequestCreation(challengeId, challengeUpdateRequest).toEntity(
      localVarReturnType
    );
  }

  /**
   * Update an existing challenge
   * Updates an existing challenge.
   * <p><b>200</b> - Challenge successfully updated
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeUpdateRequest The challengeUpdateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec updateChallengeWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeUpdateRequest challengeUpdateRequest
  ) throws RestClientResponseException {
    return updateChallengeRequestCreation(challengeId, challengeUpdateRequest);
  }
}
