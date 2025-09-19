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
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionRole;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;
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
public class ChallengeContributionApi {

  private ApiClient apiClient;

  public ChallengeContributionApi() {
    this(new ApiClient());
  }

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

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

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
   * Delete a specific challenge contribution
   * Delete a specific challenge contribution.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param organizationId The unique identifier of the organization.
   * @param role A challenge contribution role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteChallengeContributionRequestCreation(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long organizationId,
    @jakarta.annotation.Nonnull ChallengeContributionRole role
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
    // verify the required parameter 'organizationId' is set
    if (organizationId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'organizationId' when calling deleteChallengeContribution",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'role' is set
    if (role == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'role' when calling deleteChallengeContribution",
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
    pathParams.put("organizationId", organizationId);
    pathParams.put("role", role);

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
      "/challenges/{challengeId}/contributions/{organizationId}/role/{role}",
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
   * Delete a specific challenge contribution.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param organizationId The unique identifier of the organization.
   * @param role A challenge contribution role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteChallengeContribution(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long organizationId,
    @jakarta.annotation.Nonnull ChallengeContributionRole role
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteChallengeContributionRequestCreation(challengeId, organizationId, role).body(
      localVarReturnType
    );
  }

  /**
   * Delete a specific challenge contribution
   * Delete a specific challenge contribution.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param organizationId The unique identifier of the organization.
   * @param role A challenge contribution role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteChallengeContributionWithHttpInfo(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long organizationId,
    @jakarta.annotation.Nonnull ChallengeContributionRole role
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteChallengeContributionRequestCreation(challengeId, organizationId, role).toEntity(
      localVarReturnType
    );
  }

  /**
   * Delete a specific challenge contribution
   * Delete a specific challenge contribution.
   * <p><b>204</b> - Contribution deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param challengeId The unique identifier of the challenge.
   * @param organizationId The unique identifier of the organization.
   * @param role A challenge contribution role.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteChallengeContributionWithResponseSpec(
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull Long organizationId,
    @jakarta.annotation.Nonnull ChallengeContributionRole role
  ) throws RestClientResponseException {
    return deleteChallengeContributionRequestCreation(challengeId, organizationId, role);
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

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

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
}
