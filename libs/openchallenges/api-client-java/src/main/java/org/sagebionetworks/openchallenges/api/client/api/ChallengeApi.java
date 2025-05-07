package org.sagebionetworks.openchallenges.api.client.api;

import org.sagebionetworks.openchallenges.api.client.ApiClient;

import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.Challenge;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeJsonLd;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPage;

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
     * Get a challenge
     * Returns the challenge specified
     * <p><b>200</b> - A challenge
     * <p><b>404</b> - The specified resource was not found
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengeId The unique identifier of the challenge.
     * @return Challenge
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getChallengeRequestCreation(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'challengeId' is set
        if (challengeId == null) {
            throw new RestClientResponseException("Missing the required parameter 'challengeId' when calling getChallenge", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("challengeId", challengeId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        final String[] localVarAccepts = { 
            "application/json", "application/ld+json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Challenge> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/challenges/{challengeId}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
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
    public Challenge getChallenge(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        ParameterizedTypeReference<Challenge> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseEntity<Challenge> getChallengeWithHttpInfo(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        ParameterizedTypeReference<Challenge> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseSpec getChallengeWithResponseSpec(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        return getChallengeRequestCreation(challengeId);
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
    private ResponseSpec listChallengesRequestCreation(@jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery) throws RestClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageNumber", challengeSearchQuery.getPageNumber()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "pageSize", challengeSearchQuery.getPageSize()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", challengeSearchQuery.getSort()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sortSeed", challengeSearchQuery.getSortSeed()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "direction", challengeSearchQuery.getDirection()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "incentives", challengeSearchQuery.getIncentives()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "minStartDate", challengeSearchQuery.getMinStartDate()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "maxStartDate", challengeSearchQuery.getMaxStartDate()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "platforms", challengeSearchQuery.getPlatforms()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "organizations", challengeSearchQuery.getOrganizations()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "status", challengeSearchQuery.getStatus()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "submissionTypes", challengeSearchQuery.getSubmissionTypes()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "inputDataTypes", challengeSearchQuery.getInputDataTypes()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "operations", challengeSearchQuery.getOperations()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "categories", challengeSearchQuery.getCategories()));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "searchTerms", challengeSearchQuery.getSearchTerms()));
        
        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ChallengesPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/challenges", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
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
    public ChallengesPage listChallenges(@jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengesPage> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseEntity<ChallengesPage> listChallengesWithHttpInfo(@jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengesPage> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseSpec listChallengesWithResponseSpec(@jakarta.annotation.Nullable ChallengeSearchQuery challengeSearchQuery) throws RestClientResponseException {
        return listChallengesRequestCreation(challengeSearchQuery);
    }
}
