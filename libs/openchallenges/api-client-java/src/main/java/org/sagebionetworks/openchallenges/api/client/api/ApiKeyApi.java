package org.sagebionetworks.openchallenges.api.client.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.api.client.ApiClient;
import org.sagebionetworks.openchallenges.api.client.model.ApiKey;
import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.CreateApiKeyRequest;
import org.sagebionetworks.openchallenges.api.client.model.CreateApiKeyResponse;
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
public class ApiKeyApi {

  private ApiClient apiClient;

  public ApiKeyApi() {
    this(new ApiClient());
  }

  public ApiKeyApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Create API key
   * Generate a new API key for the authenticated user
   * <p><b>201</b> - API key created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param createApiKeyRequest The createApiKeyRequest parameter
   * @return CreateApiKeyResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec createApiKeyRequestCreation(
    @jakarta.annotation.Nonnull CreateApiKeyRequest createApiKeyRequest
  ) throws RestClientResponseException {
    Object postBody = createApiKeyRequest;
    // verify the required parameter 'createApiKeyRequest' is set
    if (createApiKeyRequest == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'createApiKeyRequest' when calling createApiKey",
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

    ParameterizedTypeReference<CreateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/api-keys",
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
   * Create API key
   * Generate a new API key for the authenticated user
   * <p><b>201</b> - API key created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param createApiKeyRequest The createApiKeyRequest parameter
   * @return CreateApiKeyResponse
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public CreateApiKeyResponse createApiKey(
    @jakarta.annotation.Nonnull CreateApiKeyRequest createApiKeyRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<CreateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createApiKeyRequestCreation(createApiKeyRequest).body(localVarReturnType);
  }

  /**
   * Create API key
   * Generate a new API key for the authenticated user
   * <p><b>201</b> - API key created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param createApiKeyRequest The createApiKeyRequest parameter
   * @return ResponseEntity&lt;CreateApiKeyResponse&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<CreateApiKeyResponse> createApiKeyWithHttpInfo(
    @jakarta.annotation.Nonnull CreateApiKeyRequest createApiKeyRequest
  ) throws RestClientResponseException {
    ParameterizedTypeReference<CreateApiKeyResponse> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return createApiKeyRequestCreation(createApiKeyRequest).toEntity(localVarReturnType);
  }

  /**
   * Create API key
   * Generate a new API key for the authenticated user
   * <p><b>201</b> - API key created successfully
   * <p><b>400</b> - Invalid request
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param createApiKeyRequest The createApiKeyRequest parameter
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec createApiKeyWithResponseSpec(
    @jakarta.annotation.Nonnull CreateApiKeyRequest createApiKeyRequest
  ) throws RestClientResponseException {
    return createApiKeyRequestCreation(createApiKeyRequest);
  }

  /**
   * Delete API key
   * Revoke an API key
   * <p><b>204</b> - API key deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param keyId The API key ID to delete
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteApiKeyRequestCreation(@jakarta.annotation.Nonnull UUID keyId)
    throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'keyId' is set
    if (keyId == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'keyId' when calling deleteApiKey",
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        null,
        null,
        null
      );
    }
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    pathParams.put("keyId", keyId);

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
      "/auth/api-keys/{keyId}",
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
   * Delete API key
   * Revoke an API key
   * <p><b>204</b> - API key deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param keyId The API key ID to delete
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteApiKey(@jakarta.annotation.Nonnull UUID keyId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteApiKeyRequestCreation(keyId).body(localVarReturnType);
  }

  /**
   * Delete API key
   * Revoke an API key
   * <p><b>204</b> - API key deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param keyId The API key ID to delete
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteApiKeyWithHttpInfo(@jakarta.annotation.Nonnull UUID keyId)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteApiKeyRequestCreation(keyId).toEntity(localVarReturnType);
  }

  /**
   * Delete API key
   * Revoke an API key
   * <p><b>204</b> - API key deleted successfully
   * <p><b>401</b> - Unauthorized
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param keyId The API key ID to delete
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteApiKeyWithResponseSpec(@jakarta.annotation.Nonnull UUID keyId)
    throws RestClientResponseException {
    return deleteApiKeyRequestCreation(keyId);
  }

  /**
   * List API keys
   * Get all API keys for the authenticated user
   * <p><b>200</b> - List of API keys
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return List&lt;ApiKey&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listApiKeysRequestCreation() throws RestClientResponseException {
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

    ParameterizedTypeReference<List<ApiKey>> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/auth/api-keys",
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
   * List API keys
   * Get all API keys for the authenticated user
   * <p><b>200</b> - List of API keys
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return List&lt;ApiKey&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public List<ApiKey> listApiKeys() throws RestClientResponseException {
    ParameterizedTypeReference<List<ApiKey>> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listApiKeysRequestCreation().body(localVarReturnType);
  }

  /**
   * List API keys
   * Get all API keys for the authenticated user
   * <p><b>200</b> - List of API keys
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return ResponseEntity&lt;List&lt;ApiKey&gt;&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<List<ApiKey>> listApiKeysWithHttpInfo() throws RestClientResponseException {
    ParameterizedTypeReference<List<ApiKey>> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listApiKeysRequestCreation().toEntity(localVarReturnType);
  }

  /**
   * List API keys
   * Get all API keys for the authenticated user
   * <p><b>200</b> - List of API keys
   * <p><b>401</b> - Unauthorized
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listApiKeysWithResponseSpec() throws RestClientResponseException {
    return listApiKeysRequestCreation();
  }
}
