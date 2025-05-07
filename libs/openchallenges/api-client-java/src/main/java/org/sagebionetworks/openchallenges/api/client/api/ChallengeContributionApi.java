package org.sagebionetworks.openchallenges.api.client.api;

import org.sagebionetworks.openchallenges.api.client.ApiClient;

import org.sagebionetworks.openchallenges.api.client.model.BasicError;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage;

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
     * List challenge contributions
     * List challenge contributions
     * <p><b>200</b> - Success
     * <p><b>400</b> - Invalid request
     * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
     * @param challengeId The unique identifier of the challenge.
     * @return ChallengeContributionsPage
     * @throws RestClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listChallengeContributionsRequestCreation(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        Object postBody = null;
        // verify the required parameter 'challengeId' is set
        if (challengeId == null) {
            throw new RestClientResponseException("Missing the required parameter 'challengeId' when calling listChallengeContributions", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<>();

        pathParams.put("challengeId", challengeId);

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

        ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
        return apiClient.invokeAPI("/challenges/{challengeId}/contributions", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
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
    public ChallengeContributionsPage listChallengeContributions(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseEntity<ChallengeContributionsPage> listChallengeContributionsWithHttpInfo(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        ParameterizedTypeReference<ChallengeContributionsPage> localVarReturnType = new ParameterizedTypeReference<>() {};
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
    public ResponseSpec listChallengeContributionsWithResponseSpec(@jakarta.annotation.Nonnull Long challengeId) throws RestClientResponseException {
        return listChallengeContributionsRequestCreation(challengeId);
    }
}
