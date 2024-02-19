# OrganizationApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetOrganization**](OrganizationApi.md#GetOrganization) | **GET** /organizations/{org} | Get an organization
[**ListOrganizations**](OrganizationApi.md#ListOrganizations) | **GET** /organizations | List organizations


# **GetOrganization**
> Organization GetOrganization(org)

Get an organization

Returns the organization specified

### Example
```R
library(openapi)

# Get an organization
#
# prepare function argument(s)
var_org <- "org_example" # character | The unique identifier of the organization.

api_instance <- OrganizationApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetOrganization(var_orgdata_file = "result.txt")
result <- api_instance$GetOrganization(var_org)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **character**| The unique identifier of the organization. | 

### Return type

[**Organization**](Organization.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | An organization |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **ListOrganizations**
> OrganizationsPage ListOrganizations(organization_search_query = var.organization_search_query)

List organizations

List organizations

### Example
```R
library(openapi)

# List organizations
#
# prepare function argument(s)
var_organization_search_query <- OrganizationSearchQuery$new(123, 123, c(OrganizationCategory$new()), c(ChallengeContributionRole$new()), OrganizationSort$new(), OrganizationDirection$new(), "searchTerms_example") # OrganizationSearchQuery | The search query used to find organizations. (Optional)

api_instance <- OrganizationApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ListOrganizations(organization_search_query = var_organization_search_querydata_file = "result.txt")
result <- api_instance$ListOrganizations(organization_search_query = var_organization_search_query)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **organization_search_query** | [**OrganizationSearchQuery**](.md)| The search query used to find organizations. | [optional] 

### Return type

[**OrganizationsPage**](OrganizationsPage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

