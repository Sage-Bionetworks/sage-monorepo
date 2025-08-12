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
import org.sagebionetworks.openchallenges.api.client.model.LoginRequest;
import org.sagebionetworks.openchallenges.api.client.model.LoginResponse;
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
   * User login
   * Authenticate user and return JWT token
   * <p><b>200</b> - Login successful
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param loginRequest The loginRequest parameter
   * @return LoginResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec loginRequestCreation(@jakarta.annotation.Nonnull LoginRequest loginRequest)
    throws RestClientResponseException {
    Object postBody = loginRequest;
    // verify the required parameter 'loginRequest' is set
    if (loginRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'loginRequest' when calling login",
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

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<LoginResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/login",
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
   * User login
   * Authenticate user and return JWT token
   * <p><b>200</b> - Login successful
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param loginRequest The loginRequest parameter
   * @return LoginResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public LoginResponse login(@jakarta.annotation.Nonnull LoginRequest loginRequest)
    throws RestClientResponseException {
    ParameterizedTypeReference<LoginResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return loginRequestCreation(loginRequest).body(localVarReturnType);
  }

  /**
   * User login
   * Authenticate user and return JWT token
   * <p><b>200</b> - Login successful
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param loginRequest The loginRequest parameter
   * @return ResponseEntity&lt;LoginResponse&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<LoginResponse> loginWithHttpInfo(
    @jakarta.annotation.Nonnull LoginRequest loginRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<LoginResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return loginRequestCreation(loginRequest).toEntity(localVarReturnType);
  }

  /**
   * User login
   * Authenticate user and return JWT token
   * <p><b>200</b> - Login successful
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param loginRequest The loginRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec loginWithResponseSpec(@jakarta.annotation.Nonnull LoginRequest loginRequest)
    throws RestClientResponseException {
    return loginRequestCreation(loginRequest);
  }
}
