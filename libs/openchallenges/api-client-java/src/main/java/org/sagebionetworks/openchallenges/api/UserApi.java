package org.sagebionetworks.openchallenges.api;

import org.sagebionetworks.openchallenges.ApiClient;

import org.openapitools.client.model.BasicError;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserCreateRequest;
import org.openapitools.client.model.UserCreateResponse;
import org.openapitools.client.model.UsersPage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientResponseException;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.13.0")
public class UserApi {
    private ApiClient apiClient;

    public UserApi() {
        this(new ApiClient());
    }

    @Autowired
    public UserApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create a user
     * Create a user with the specified account name
     * <p><b>201</b> - Account created
     * <p><b>400</b> - Invalid request
     * <p><b>409</b> - The request conflicts with current state of the target resource
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userCreateRequest The userCreateRequest parameter
     * @return UserCreateResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createUserRequestCreation(@jakarta.annotation.Nonnull UserCreateRequest userCreateRequest) throws RestClientResponseException {
        Object postBody = userCreateRequest;
        // verify the required parameter 'userCreateRequest' is set
        if (userCreateRequest == null) {
            throw new RestClientResponseException("Missing the required parameter 'userCreateRequest' when calling createUser", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UserCreateResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/users/register", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create a user
     * Create a user with the specified account name
     * <p><b>201</b> - Account created
     * <p><b>400</b> - Invalid request
     * <p><b>409</b> - The request conflicts with current state of the target resource
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userCreateRequest The userCreateRequest parameter
     * @return UserCreateResponse
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public UserCreateResponse createUser(@jakarta.annotation.Nonnull UserCreateRequest userCreateRequest) throws RestClientResponseException {
        ParameterizedTypeReference<UserCreateResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return createUserRequestCreation(userCreateRequest).body(localVarReturnType);
    }

    /**
     * Create a user
     * Create a user with the specified account name
     * <p><b>201</b> - Account created
     * <p><b>400</b> - Invalid request
     * <p><b>409</b> - The request conflicts with current state of the target resource
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userCreateRequest The userCreateRequest parameter
     * @return ResponseEntity&lt;UserCreateResponse&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<UserCreateResponse> createUserWithHttpInfo(@jakarta.annotation.Nonnull UserCreateRequest userCreateRequest) throws RestClientResponseException {
        ParameterizedTypeReference<UserCreateResponse> localVarReturnType = new ParameterizedTypeReference<>() {};
        return createUserRequestCreation(userCreateRequest).toEntity(localVarReturnType);
    }

    /**
     * Create a user
     * Create a user with the specified account name
     * <p><b>201</b> - Account created
     * <p><b>400</b> - Invalid request
     * <p><b>409</b> - The request conflicts with current state of the target resource
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userCreateRequest The userCreateRequest parameter
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec createUserWithResponseSpec(@jakarta.annotation.Nonnull UserCreateRequest userCreateRequest) throws RestClientResponseException {
        return createUserRequestCreation(userCreateRequest);
    }
    /**
     * Delete a user
     * Deletes the user specified
     * <p><b>200</b> - Deleted
     * <p><b>400</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return Object
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteUserRequestCreation(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new RestClientResponseException("Missing the required parameter 'userId' when calling deleteUser", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("userId", userId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Object> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/users/{userId}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete a user
     * Deletes the user specified
     * <p><b>200</b> - Deleted
     * <p><b>400</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return Object
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public Object deleteUser(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        ParameterizedTypeReference<Object> localVarReturnType = new ParameterizedTypeReference<>() {};
        return deleteUserRequestCreation(userId).body(localVarReturnType);
    }

    /**
     * Delete a user
     * Deletes the user specified
     * <p><b>200</b> - Deleted
     * <p><b>400</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return ResponseEntity&lt;Object&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Object> deleteUserWithHttpInfo(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        ParameterizedTypeReference<Object> localVarReturnType = new ParameterizedTypeReference<>() {};
        return deleteUserRequestCreation(userId).toEntity(localVarReturnType);
    }

    /**
     * Delete a user
     * Deletes the user specified
     * <p><b>200</b> - Deleted
     * <p><b>400</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteUserWithResponseSpec(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        return deleteUserRequestCreation(userId);
    }
    /**
     * Get a user
     * Returns the user specified
     * <p><b>200</b> - A user
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return User
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getUserRequestCreation(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new RestClientResponseException("Missing the required parameter 'userId' when calling getUser", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("userId", userId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<User> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/users/{userId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get a user
     * Returns the user specified
     * <p><b>200</b> - A user
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return User
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public User getUser(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        ParameterizedTypeReference<User> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getUserRequestCreation(userId).body(localVarReturnType);
    }

    /**
     * Get a user
     * Returns the user specified
     * <p><b>200</b> - A user
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return ResponseEntity&lt;User&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<User> getUserWithHttpInfo(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        ParameterizedTypeReference<User> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getUserRequestCreation(userId).toEntity(localVarReturnType);
    }

    /**
     * Get a user
     * Returns the user specified
     * <p><b>200</b> - A user
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param userId The unique identifier of the user, either the user account ID or login
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getUserWithResponseSpec(@jakarta.annotation.Nonnull Long userId) throws RestClientResponseException {
        return getUserRequestCreation(userId);
    }
    /**
     * Get all users
     * Returns the users
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param pageNumber The page number.
     * @param pageSize The number of items in a single page.
     * @return UsersPage
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listUsersRequestCreation(@jakarta.annotation.Nullable Integer pageNumber, @jakarta.annotation.Nullable Integer pageSize) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageNumber", pageNumber));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", pageSize));
        
        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UsersPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/users", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get all users
     * Returns the users
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param pageNumber The page number.
     * @param pageSize The number of items in a single page.
     * @return UsersPage
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public UsersPage listUsers(@jakarta.annotation.Nullable Integer pageNumber, @jakarta.annotation.Nullable Integer pageSize) throws RestClientResponseException {
        ParameterizedTypeReference<UsersPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listUsersRequestCreation(pageNumber, pageSize).body(localVarReturnType);
    }

    /**
     * Get all users
     * Returns the users
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param pageNumber The page number.
     * @param pageSize The number of items in a single page.
     * @return ResponseEntity&lt;UsersPage&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<UsersPage> listUsersWithHttpInfo(@jakarta.annotation.Nullable Integer pageNumber, @jakarta.annotation.Nullable Integer pageSize) throws RestClientResponseException {
        ParameterizedTypeReference<UsersPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listUsersRequestCreation(pageNumber, pageSize).toEntity(localVarReturnType);
    }

    /**
     * Get all users
     * Returns the users
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param pageNumber The page number.
     * @param pageSize The number of items in a single page.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listUsersWithResponseSpec(@jakarta.annotation.Nullable Integer pageNumber, @jakarta.annotation.Nullable Integer pageSize) throws RestClientResponseException {
        return listUsersRequestCreation(pageNumber, pageSize);
    }
}
