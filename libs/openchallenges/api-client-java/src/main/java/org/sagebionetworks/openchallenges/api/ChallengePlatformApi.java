package org.sagebionetworks.openchallenges.api;

import org.sagebionetworks.openchallenges.ApiClient;

import org.openapitools.client.model.BasicError;
import org.openapitools.client.model.ChallengePlatform;
import org.openapitools.client.model.ChallengePlatformSearchQuery;
import org.openapitools.client.model.ChallengePlatformsPage;

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
public class ChallengePlatformApi {
    private ApiClient apiClient;

    public ChallengePlatformApi() {
        this(new ApiClient());
    }

    @Autowired
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
     * Get a challenge platform
     * Returns the challenge platform specified
     * <p><b>200</b> - Success
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformName The unique identifier of the challenge platform.
     * @return ChallengePlatform
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getChallengePlatformRequestCreation(@jakarta.annotation.Nonnull String challengePlatformName) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'challengePlatformName' is set
        if (challengePlatformName == null) {
            throw new RestClientResponseException("Missing the required parameter 'challengePlatformName' when calling getChallengePlatform", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("challengePlatformName", challengePlatformName);

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

        ParameterizedTypeReference<ChallengePlatform> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/challengePlatforms/{challengePlatformName}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get a challenge platform
     * Returns the challenge platform specified
     * <p><b>200</b> - Success
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformName The unique identifier of the challenge platform.
     * @return ChallengePlatform
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ChallengePlatform getChallengePlatform(@jakarta.annotation.Nonnull String challengePlatformName) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengePlatform> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getChallengePlatformRequestCreation(challengePlatformName).body(localVarReturnType);
    }

    /**
     * Get a challenge platform
     * Returns the challenge platform specified
     * <p><b>200</b> - Success
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformName The unique identifier of the challenge platform.
     * @return ResponseEntity&lt;ChallengePlatform&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ChallengePlatform> getChallengePlatformWithHttpInfo(@jakarta.annotation.Nonnull String challengePlatformName) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengePlatform> localVarReturnType = new ParameterizedTypeReference<>() {};
        return getChallengePlatformRequestCreation(challengePlatformName).toEntity(localVarReturnType);
    }

    /**
     * Get a challenge platform
     * Returns the challenge platform specified
     * <p><b>200</b> - Success
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformName The unique identifier of the challenge platform.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getChallengePlatformWithResponseSpec(@jakarta.annotation.Nonnull String challengePlatformName) throws RestClientResponseException {
        return getChallengePlatformRequestCreation(challengePlatformName);
    }
    /**
     * List challenge platforms
     * List challenge platforms
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformSearchQuery The search query used to find challenge platforms.
     * @return ChallengePlatformsPage
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listChallengePlatformsRequestCreation(@jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageNumber", challengePlatformSearchQuery.getPageNumber()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", challengePlatformSearchQuery.getPageSize()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", challengePlatformSearchQuery.getSort()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "direction", challengePlatformSearchQuery.getDirection()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "slugs", challengePlatformSearchQuery.getSlugs()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "searchTerms", challengePlatformSearchQuery.getSearchTerms()));
        
        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/challengePlatforms", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List challenge platforms
     * List challenge platforms
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformSearchQuery The search query used to find challenge platforms.
     * @return ChallengePlatformsPage
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ChallengePlatformsPage listChallengePlatforms(@jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listChallengePlatformsRequestCreation(challengePlatformSearchQuery).body(localVarReturnType);
    }

    /**
     * List challenge platforms
     * List challenge platforms
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformSearchQuery The search query used to find challenge platforms.
     * @return ResponseEntity&lt;ChallengePlatformsPage&gt;
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ChallengePlatformsPage> listChallengePlatformsWithHttpInfo(@jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengePlatformsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return listChallengePlatformsRequestCreation(challengePlatformSearchQuery).toEntity(localVarReturnType);
    }

    /**
     * List challenge platforms
     * List challenge platforms
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengePlatformSearchQuery The search query used to find challenge platforms.
     * @return ResponseSpec
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listChallengePlatformsWithResponseSpec(@jakarta.annotation.Nullable ChallengePlatformSearchQuery challengePlatformSearchQuery) throws RestClientResponseException {
        return listChallengePlatformsRequestCreation(challengePlatformSearchQuery);
    }
}
