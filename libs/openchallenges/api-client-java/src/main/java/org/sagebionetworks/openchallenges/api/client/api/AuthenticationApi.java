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
import org.sagebionetworks.openchallenges.api.client.model.UpdateUserProfileRequest;
import org.sagebionetworks.openchallenges.api.client.model.UserProfile;
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
public class AuthenticationApi {

  private ApiClient apiClient;

  public AuthenticationApi() {
    this(new ApiClient());
  }

  public AuthenticationApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Get user profile
   * Get the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile information
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return UserProfile
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getUserProfileRequestCreation() throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiKey", "jwtBearer" };

    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/profile",
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
   * Get user profile
   * Get the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile information
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return UserProfile
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public UserProfile getUserProfile() throws RestClientResponseException {
    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getUserProfileRequestCreation().body(localVarReturnType);
  }

  /**
   * Get user profile
   * Get the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile information
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return ResponseEntity&lt;UserProfile&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<UserProfile> getUserProfileWithHttpInfo()
    throws RestClientResponseException {
    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getUserProfileRequestCreation().toEntity(localVarReturnType);
  }

  /**
   * Get user profile
   * Get the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile information
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getUserProfileWithResponseSpec() throws RestClientResponseException {
    return getUserProfileRequestCreation();
  }

  /**
   * Update user profile
   * Update the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param updateUserProfileRequest The updateUserProfileRequest parameter
   * @return UserProfile
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec updateUserProfileRequestCreation(
    @jakarta.annotation.Nonnull UpdateUserProfileRequest updateUserProfileRequest
  ) throws RestClientResponseException {
    Object postBody = updateUserProfileRequest;
    // verify the required parameter 'updateUserProfileRequest' is set
    if (updateUserProfileRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'updateUserProfileRequest' when calling updateUserProfile",
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

    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/profile",
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
   * Update user profile
   * Update the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param updateUserProfileRequest The updateUserProfileRequest parameter
   * @return UserProfile
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public UserProfile updateUserProfile(
    @jakarta.annotation.Nonnull UpdateUserProfileRequest updateUserProfileRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateUserProfileRequestCreation(updateUserProfileRequest).body(localVarReturnType);
  }

  /**
   * Update user profile
   * Update the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param updateUserProfileRequest The updateUserProfileRequest parameter
   * @return ResponseEntity&lt;UserProfile&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<UserProfile> updateUserProfileWithHttpInfo(
    @jakarta.annotation.Nonnull UpdateUserProfileRequest updateUserProfileRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<UserProfile> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return updateUserProfileRequestCreation(updateUserProfileRequest).toEntity(localVarReturnType);
  }

  /**
   * Update user profile
   * Update the authenticated user&#39;s profile information
   * <p><b>200</b> - User profile updated successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param updateUserProfileRequest The updateUserProfileRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec updateUserProfileWithResponseSpec(
    @jakarta.annotation.Nonnull UpdateUserProfileRequest updateUserProfileRequest
  ) throws RestClientResponseException {
    return updateUserProfileRequestCreation(updateUserProfileRequest);
  }
}
