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
import org.sagebionetworks.openchallenges.api.client.model.ChallengeParticipation;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeParticipationCreateRequest;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeParticipationRole;
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
public class ChallengeParticipationApi {

  private ApiClient apiClient;

  public ChallengeParticipationApi() {
    this(new ApiClient());
  }

  @Autowired
  public ChallengeParticipationApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create a new challenge participation
   * Creates a new challenge participation.
   * <p><b>201</b> - Participation created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeParticipationCreateRequest The challengeParticipationCreateRequest parameter
   * @return ChallengeParticipation
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec createChallengeParticipationRequestCreation(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull ChallengeParticipationCreateRequest challengeParticipationCreateRequest
  ) throws RestClientResponseException {
    Object postBody = challengeParticipationCreateRequest;
    // verify the required parameter 'org' is set
    if (org == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'org' when calling createChallengeParticipation",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeParticipationCreateRequest' is set
    if (challengeParticipationCreateRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeParticipationCreateRequest' when calling createChallengeParticipation",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("org", org);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<ChallengeParticipation> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/organizations/{org}/participations",
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
   * Create a new challenge participation
   * Creates a new challenge participation.
   * <p><b>201</b> - Participation created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeParticipationCreateRequest The challengeParticipationCreateRequest parameter
   * @return ChallengeParticipation
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ChallengeParticipation createChallengeParticipation(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull ChallengeParticipationCreateRequest challengeParticipationCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeParticipation> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeParticipationRequestCreation(
      org,
      challengeParticipationCreateRequest
    ).body(localVarReturnType);
  }

  /**
   * Create a new challenge participation
   * Creates a new challenge participation.
   * <p><b>201</b> - Participation created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeParticipationCreateRequest The challengeParticipationCreateRequest parameter
   * @return ResponseEntity&lt;ChallengeParticipation&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ChallengeParticipation> createChallengeParticipationWithHttpInfo(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull ChallengeParticipationCreateRequest challengeParticipationCreateRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ChallengeParticipation> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createChallengeParticipationRequestCreation(
      org,
      challengeParticipationCreateRequest
    ).toEntity(localVarReturnType);
  }

  /**
   * Create a new challenge participation
   * Creates a new challenge participation.
   * <p><b>201</b> - Participation created successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>409</b> - The request conflicts with current state of the target resource
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeParticipationCreateRequest The challengeParticipationCreateRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec createChallengeParticipationWithResponseSpec(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull ChallengeParticipationCreateRequest challengeParticipationCreateRequest
  ) throws RestClientResponseException {
    return createChallengeParticipationRequestCreation(org, challengeParticipationCreateRequest);
  }

  /**
   * Delete a specific challenge participation
   * Delete a specific challenge participation.
   * <p><b>204</b> - Participation deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeId The unique identifier of the challenge.
   * @param role A challenge participation role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteChallengeParticipationRequestCreation(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeParticipationRole role
  ) throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'org' is set
    if (org == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'org' when calling deleteChallengeParticipation",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // verify the required parameter 'challengeId' is set
    if (challengeId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'challengeId' when calling deleteChallengeParticipation",
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
        "Missing the required parameter 'role' when calling deleteChallengeParticipation",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("org", org);
    pathParams.put("challengeId", challengeId);
    pathParams.put("role", role);

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
      "/organizations/{org}/participations/{challengeId}/roles/{role}",
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
   * Delete a specific challenge participation
   * Delete a specific challenge participation.
   * <p><b>204</b> - Participation deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeId The unique identifier of the challenge.
   * @param role A challenge participation role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteChallengeParticipation(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeParticipationRole role
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteChallengeParticipationRequestCreation(org, challengeId, role).body(localVarReturnType);
  }

  /**
   * Delete a specific challenge participation
   * Delete a specific challenge participation.
   * <p><b>204</b> - Participation deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeId The unique identifier of the challenge.
   * @param role A challenge participation role.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteChallengeParticipationWithHttpInfo(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeParticipationRole role
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteChallengeParticipationRequestCreation(org, challengeId, role).toEntity(
      localVarReturnType
    );
  }

  /**
   * Delete a specific challenge participation
   * Delete a specific challenge participation.
   * <p><b>204</b> - Participation deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @param challengeId The unique identifier of the challenge.
   * @param role A challenge participation role.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteChallengeParticipationWithResponseSpec(
    @jakarta.annotation.Nonnull String org,
    @jakarta.annotation.Nonnull Long challengeId,
    @jakarta.annotation.Nonnull ChallengeParticipationRole role
  ) throws RestClientResponseException {
    return deleteChallengeParticipationRequestCreation(org, challengeId, role);
  }
}
