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
import org.sagebionetworks.openchallenges.api.client.model.Organization;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationsPage;
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
public class OrganizationApi {

  private ApiClient apiClient;

  public OrganizationApi() {
    this(new ApiClient());
  }

  @Autowired
  public OrganizationApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Delete an organization
   * Deletes the organization specified
   * <p><b>204</b> - Organization successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec deleteOrganizationRequestCreation(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'org' is set
    if (org == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'org' when calling deleteOrganization",
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

    final String[] localVarAccepts = { "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "apiBearerAuth" };

    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/organizations/{org}",
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
   * Delete an organization
   * Deletes the organization specified
   * <p><b>204</b> - Organization successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public void deleteOrganization(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    deleteOrganizationRequestCreation(org).body(localVarReturnType);
  }

  /**
   * Delete an organization
   * Deletes the organization specified
   * <p><b>204</b> - Organization successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Void> deleteOrganizationWithHttpInfo(
    @jakarta.annotation.Nonnull String org
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<>() {};
    return deleteOrganizationRequestCreation(org).toEntity(localVarReturnType);
  }

  /**
   * Delete an organization
   * Deletes the organization specified
   * <p><b>204</b> - Organization successfully deleted
   * <p><b>401</b> - Unauthorized
   * <p><b>403</b> - The user does not have the permission to perform this action
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec deleteOrganizationWithResponseSpec(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    return deleteOrganizationRequestCreation(org);
  }

  /**
   * Get an organization
   * Returns the organization specified
   * <p><b>200</b> - An organization
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @return Organization
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec getOrganizationRequestCreation(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    Object postBody = null;
    // verify the required parameter 'org' is set
    if (org == null) {
      throw new RestClientResponseException(
        "Missing the required parameter 'org' when calling getOrganization",
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
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<Organization> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/organizations/{org}",
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
   * Get an organization
   * Returns the organization specified
   * <p><b>200</b> - An organization
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @return Organization
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public Organization getOrganization(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    ParameterizedTypeReference<Organization> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getOrganizationRequestCreation(org).body(localVarReturnType);
  }

  /**
   * Get an organization
   * Returns the organization specified
   * <p><b>200</b> - An organization
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @return ResponseEntity&lt;Organization&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<Organization> getOrganizationWithHttpInfo(
    @jakarta.annotation.Nonnull String org
  ) throws RestClientResponseException {
    ParameterizedTypeReference<Organization> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return getOrganizationRequestCreation(org).toEntity(localVarReturnType);
  }

  /**
   * Get an organization
   * Returns the organization specified
   * <p><b>200</b> - An organization
   * <p><b>404</b> - The specified resource was not found
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param org The id or login of the organization.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec getOrganizationWithResponseSpec(@jakarta.annotation.Nonnull String org)
    throws RestClientResponseException {
    return getOrganizationRequestCreation(org);
  }

  /**
   * List organizations
   * List organizations
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param organizationSearchQuery The search query used to find organizations.
   * @return OrganizationsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  private ResponseSpec listOrganizationsRequestCreation(
    @jakarta.annotation.Nullable OrganizationSearchQuery organizationSearchQuery
  ) throws RestClientResponseException {
    Object postBody = null;
    // create path and map variables
    final Map<String, Object> pathParams = new HashMap<>();

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "pageNumber",
        organizationSearchQuery.getPageNumber()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "pageSize", organizationSearchQuery.getPageSize())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "categories",
        organizationSearchQuery.getCategories()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "challengeContributionRoles",
        organizationSearchQuery.getChallengeContributionRoles()
      )
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "sort", organizationSearchQuery.getSort())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "direction", organizationSearchQuery.getDirection())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(null, "ids", organizationSearchQuery.getIds())
    );
    queryParams.putAll(
      apiClient.parameterToMultiValueMap(
        null,
        "searchTerms",
        organizationSearchQuery.getSearchTerms()
      )
    );

    final String[] localVarAccepts = { "application/json", "application/problem+json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {};
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {};

    ParameterizedTypeReference<OrganizationsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return apiClient.invokeAPI(
      "/organizations",
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
   * List organizations
   * List organizations
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param organizationSearchQuery The search query used to find organizations.
   * @return OrganizationsPage
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public OrganizationsPage listOrganizations(
    @jakarta.annotation.Nullable OrganizationSearchQuery organizationSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<OrganizationsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listOrganizationsRequestCreation(organizationSearchQuery).body(localVarReturnType);
  }

  /**
   * List organizations
   * List organizations
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param organizationSearchQuery The search query used to find organizations.
   * @return ResponseEntity&lt;OrganizationsPage&gt;
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseEntity<OrganizationsPage> listOrganizationsWithHttpInfo(
    @jakarta.annotation.Nullable OrganizationSearchQuery organizationSearchQuery
  ) throws RestClientResponseException {
    ParameterizedTypeReference<OrganizationsPage> localVarReturnType =
      new ParameterizedTypeReference<>() {};
    return listOrganizationsRequestCreation(organizationSearchQuery).toEntity(localVarReturnType);
  }

  /**
   * List organizations
   * List organizations
   * <p><b>200</b> - Success
   * <p><b>400</b> - Invalid request
   * <p><b>500</b> - The request cannot be fulfilled due to an unexpected server error
   * @param organizationSearchQuery The search query used to find organizations.
   * @return ResponseSpec
   * @throws RestClientResponseException if an error occurs while attempting to invoke the API
   */
  public ResponseSpec listOrganizationsWithResponseSpec(
    @jakarta.annotation.Nullable OrganizationSearchQuery organizationSearchQuery
  ) throws RestClientResponseException {
    return listOrganizationsRequestCreation(organizationSearchQuery);
  }
}
