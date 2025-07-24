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
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContribution;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionCreateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionUpdateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;
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
public class ChallengeContributionApi {

  private ApiClient apiClient;

  public ChallengeContributionApi() {
    this(new ApiClient());
  }

  @Autowired
  public ChallengeContributionApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create a new contribution for a challenge
   * Creates a new contribution record associated with a challenge ID.
   * <p><b>201</b> - Contribution created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionCreateRequest The challengeContributionCreateRequest parameter
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec createChallengeContributionRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeContributionCreateRequest challengeContributionCreateRequest
  ) throws RestClientResponseException {
    Object postBody = challengeContributionCreateRequest;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling createChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeContributionCreateRequest' is set
    if (challengeContributionCreateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeContributionCreateRequest' when calling createChallengeContribution",
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

    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}/contributions",
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
   * Create a new contribution for a challenge
   * Creates a new contribution record associated with a challenge ID.
   * <p><b>201</b> - Contribution created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionCreateRequest The challengeContributionCreateRequest parameter
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeContribution createChallengeContribution(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeContributionCreateRequest challengeContributionCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeContributionRequestCreation(
      challengeId,
      challengeContributionCreateRequest
    ).body(localVarReturnType);
  }

  /**
   * Create a new contribution for a challenge
   * Creates a new contribution record associated with a challenge ID.
   * <p><b>201</b> - Contribution created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionCreateRequest The challengeContributionCreateRequest parameter
   * @return ResponseEntity&lt;ChallengeContribution&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeContribution> createChallengeContributionWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeContributionCreateRequest challengeContributionCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeContributionRequestCreation(
      challengeId,
      challengeContributionCreateRequest
    ).toEntity(localVarReturnType);
  }

  /**
   * Create a new contribution for a challenge
   * Creates a new contribution record associated with a challenge ID.
   * <p><b>201</b> - Contribution created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionCreateRequest The challengeContributionCreateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec createChallengeContributionWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeContributionCreateRequest challengeContributionCreateRequest
  ) throws RestClientResponseException {
    return createChallengeContributionRequestCreation(
      challengeId,
      challengeContributionCreateRequest
    );
  }

  /**
   * Delete all contributions for a specific challenge
   * Deletes all associated contributions for a given challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteAllChallengeContributionsRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling deleteAllChallengeContributions",
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
      "/challenges/{challengeId}/contributions",
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
   * Delete all contributions for a specific challenge
   * Deletes all associated contributions for a given challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteAllChallengeContributions(@jakarta.annotation.Nonnull Long challengeId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteAllChallengeContributionsRequestCreation(challengeId).body(localVarReturnType);
  }

  /**
   * Delete all contributions for a specific challenge
   * Deletes all associated contributions for a given challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteAllChallengeContributionsWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteAllChallengeContributionsRequestCreation(challengeId).toEntity(localVarReturnType);
  }

  /**
   * Delete all contributions for a specific challenge
   * Deletes all associated contributions for a given challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Deletion successful
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteAllChallengeContributionsWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    return deleteAllChallengeContributionsRequestCreation(challengeId);
  }

  /**
   * Delete a specific challenge contribution
   * Deletes a specific contribution record for a challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteChallengeContributionRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling deleteChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeContributionId' is set
    if (challengeContributionId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeContributionId' when calling deleteChallengeContribution",
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
    pathParams.put("challengeContributionId", challengeContributionId);

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
      "/challenges/{challengeId}/contributions/{challengeContributionId}",
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
   * Delete a specific challenge contribution
   * Deletes a specific contribution record for a challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteChallengeContribution(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteChallengeContributionRequestCreation(challengeId, challengeContributionId).body(
      localVarReturnType
    );
  }

  /**
   * Delete a specific challenge contribution
   * Deletes a specific contribution record for a challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteChallengeContributionWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteChallengeContributionRequestCreation(
      challengeId,
      challengeContributionId
    ).toEntity(localVarReturnType);
  }

  /**
   * Delete a specific challenge contribution
   * Deletes a specific contribution record for a challenge, identified by its ID. This action is irreversible.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteChallengeContributionWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    return deleteChallengeContributionRequestCreation(challengeId, challengeContributionId);
  }

  /**
   * Get a specific challenge contribution
   * Retrieves a specific contribution record for a challenge, identified by its ID.
   * <p><b>200</b> - Challenge contribution retrieved successfully
   * <p><b>400</b> - Invalid request
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getChallengeContributionRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling getChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeContributionId' is set
    if (challengeContributionId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeContributionId' when calling getChallengeContribution",
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
    pathParams.put("challengeContributionId", challengeContributionId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}/contributions/{challengeContributionId}",
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
   * Get a specific challenge contribution
   * Retrieves a specific contribution record for a challenge, identified by its ID.
   * <p><b>200</b> - Challenge contribution retrieved successfully
   * <p><b>400</b> - Invalid request
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeContribution getChallengeContribution(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeContributionRequestCreation(challengeId, challengeContributionId).body(
      localVarReturnType
    );
  }

  /**
   * Get a specific challenge contribution
   * Retrieves a specific contribution record for a challenge, identified by its ID.
   * <p><b>200</b> - Challenge contribution retrieved successfully
   * <p><b>400</b> - Invalid request
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @return ResponseEntity&lt;ChallengeContribution&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeContribution> getChallengeContributionWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getChallengeContributionRequestCreation(challengeId, challengeContributionId).toEntity(
      localVarReturnType
    );
  }

  /**
   * Get a specific challenge contribution
   * Retrieves a specific contribution record for a challenge, identified by its ID.
   * <p><b>200</b> - Challenge contribution retrieved successfully
   * <p><b>400</b> - Invalid request
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getChallengeContributionWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId
  ) throws RestClientResponseException {
    return getChallengeContributionRequestCreation(challengeId, challengeContributionId);
  }

  /**
   * List challenge contributions
   * List challenge contributions
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ChallengeContributionsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listChallengeContributionsRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling listChallengeContributions",
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

    ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}/contributions",
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
   * List challenge contributions
   * List challenge contributions
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ChallengeContributionsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeContributionsPage listChallengeContributions(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengeContributionsRequestCreation(challengeId).body(localVarReturnType);
  }

  /**
   * List challenge contributions
   * List challenge contributions
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseEntity&lt;ChallengeContributionsPage&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeContributionsPage> listChallengeContributionsWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listChallengeContributionsRequestCreation(challengeId).toEntity(localVarReturnType);
  }

  /**
   * List challenge contributions
   * List challenge contributions
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listChallengeContributionsWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId
  ) throws RestClientResponseException {
    return listChallengeContributionsRequestCreation(challengeId);
  }

  /**
   * Update an existing challenge contribution
   * Updates an existing challenge contribution.
   * <p><b>200</b> - Contribution updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @param challengeContributionUpdateRequest The challengeContributionUpdateRequest parameter
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec updateChallengeContributionRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId,
    @jakarta.annotation.Nonnull ChallengeContributionUpdateRequest challengeContributionUpdateRequest
  ) throws RestClientResponseException {
    Object postBody = challengeContributionUpdateRequest;
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling updateChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeContributionId' is set
    if (challengeContributionId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeContributionId' when calling updateChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeContributionUpdateRequest' is set
    if (challengeContributionUpdateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeContributionUpdateRequest' when calling updateChallengeContribution",
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
    pathParams.put("challengeContributionId", challengeContributionId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/challenges/{challengeId}/contributions/{challengeContributionId}",
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
   * Update an existing challenge contribution
   * Updates an existing challenge contribution.
   * <p><b>200</b> - Contribution updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @param challengeContributionUpdateRequest The challengeContributionUpdateRequest parameter
   * @return ChallengeContribution
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeContribution updateChallengeContribution(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId,
    @jakarta.annotation.Nonnull ChallengeContributionUpdateRequest challengeContributionUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengeContributionRequestCreation(
      challengeId,
      challengeContributionId,
      challengeContributionUpdateRequest
    ).body(localVarReturnType);
  }

  /**
   * Update an existing challenge contribution
   * Updates an existing challenge contribution.
   * <p><b>200</b> - Contribution updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @param challengeContributionUpdateRequest The challengeContributionUpdateRequest parameter
   * @return ResponseEntity&lt;ChallengeContribution&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeContribution> updateChallengeContributionWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId,
    @jakarta.annotation.Nonnull ChallengeContributionUpdateRequest challengeContributionUpdateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeContribution> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateChallengeContributionRequestCreation(
      challengeId,
      challengeContributionId,
      challengeContributionUpdateRequest
    ).toEntity(localVarReturnType);
  }

  /**
   * Update an existing challenge contribution
   * Updates an existing challenge contribution.
   * <p><b>200</b> - Contribution updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param challengeContributionId The unique identifier of a challenge contribution
   * @param challengeContributionUpdateRequest The challengeContributionUpdateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec updateChallengeContributionWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long challengeContributionId,
    @jakarta.annotation.Nonnull ChallengeContributionUpdateRequest challengeContributionUpdateRequest
  ) throws RestClientResponseException {
    return updateChallengeContributionRequestCreation(
      challengeId,
      challengeContributionId,
      challengeContributionUpdateRequest
    );
  }
}
