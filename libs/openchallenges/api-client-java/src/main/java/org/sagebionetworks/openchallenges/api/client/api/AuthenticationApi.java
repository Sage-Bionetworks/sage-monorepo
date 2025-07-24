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
import org.sagebionetworks.openchallenges.api.client.model.ValidateApiKeyRequest;
import org.sagebionetworks.openchallenges.api.client.model.ValidateApiKeyResponse;
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
public class AuthenticationApi {

  private ApiClient apiClient;

  public AuthenticationApi() {
    this(new ApiClient());
  }

  @Autowired
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

  /**
   * Validate API key
   * Internal endpoint to validate API keys (used by other services)
   * <p><b>200</b> - API key is valid
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param validateApiKeyRequest The validateApiKeyRequest parameter
   * @return ValidateApiKeyResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec validateApiKeyRequestCreation(
    @jakarta.annotation.Nonnull ValidateApiKeyRequest validateApiKeyRequest
  ) throws RestClientResponseException {
    Object postBody = validateApiKeyRequest;
    // verify the required parameter 'validateApiKeyRequest' is set
    if (validateApiKeyRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'validateApiKeyRequest' when calling validateApiKey",
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

    ParameterizedTypeReference<ValidateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/validate",
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
   * Validate API key
   * Internal endpoint to validate API keys (used by other services)
   * <p><b>200</b> - API key is valid
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param validateApiKeyRequest The validateApiKeyRequest parameter
   * @return ValidateApiKeyResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ValidateApiKeyResponse validateApiKey(
    @jakarta.annotation.Nonnull ValidateApiKeyRequest validateApiKeyRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ValidateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return validateApiKeyRequestCreation(validateApiKeyRequest).body(localVarReturnType);
  }

  /**
   * Validate API key
   * Internal endpoint to validate API keys (used by other services)
   * <p><b>200</b> - API key is valid
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param validateApiKeyRequest The validateApiKeyRequest parameter
   * @return ResponseEntity&lt;ValidateApiKeyResponse&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<ValidateApiKeyResponse> validateApiKeyWithHttpInfo(
    @jakarta.annotation.Nonnull ValidateApiKeyRequest validateApiKeyRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<ValidateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return validateApiKeyRequestCreation(validateApiKeyRequest).toEntity(localVarReturnType);
  }

  /**
   * Validate API key
   * Internal endpoint to validate API keys (used by other services)
   * <p><b>200</b> - API key is valid
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param validateApiKeyRequest The validateApiKeyRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec validateApiKeyWithResponseSpec(
    @jakarta.annotation.Nonnull ValidateApiKeyRequest validateApiKeyRequest
  ) throws RestClientResponseException {
    return validateApiKeyRequestCreation(validateApiKeyRequest);
  }
}
