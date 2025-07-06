# openchallenges.OrganizationApi

All URIs are relative to _http://localhost/v1_

| Method                                                          | HTTP request                 | Description         |
| --------------------------------------------------------------- | ---------------------------- | ------------------- |
| [**get_organization**](OrganizationApi.md#get_organization)     | **GET** /organizations/{org} | Get an organization |
| [**list_organizations**](OrganizationApi.md#list_organizations) | **GET** /organizations       | List organizations  |

# **get_organization**

> Organization get_organization(org)

Get an organization

Returns the organization specified

### Example

```python
import openchallenges
from openchallenges.models.organization import Organization
from openchallenges.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges.OrganizationApi(api_client)
    org = 'dream' # str | The id or login of the organization.

    try:
        # Get an organization
        api_response = api_instance.get_organization(org)
        print("The response of OrganizationApi->get_organization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_organization: %s\n" % e)
```

### Parameters

| Name    | Type    | Description                          | Notes |
| ------- | ------- | ------------------------------------ | ----- |
| **org** | **str** | The id or login of the organization. |

### Return type

[**Organization**](Organization.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | An organization                                                   | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_organizations**

> OrganizationsPage list_organizations(organization_search_query=organization_search_query)

List organizations

List organizations

### Example

```python
import openchallenges
from openchallenges.models.organization_search_query import OrganizationSearchQuery
from openchallenges.models.organizations_page import OrganizationsPage
from openchallenges.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges.OrganizationApi(api_client)
    organization_search_query = openchallenges.OrganizationSearchQuery() # OrganizationSearchQuery | The search query used to find organizations. (optional)

    try:
        # List organizations
        api_response = api_instance.list_organizations(organization_search_query=organization_search_query)
        print("The response of OrganizationApi->list_organizations:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->list_organizations: %s\n" % e)
```

### Parameters

| Name                          | Type                               | Description                                  | Notes      |
| ----------------------------- | ---------------------------------- | -------------------------------------------- | ---------- |
| **organization_search_query** | [**OrganizationSearchQuery**](.md) | The search query used to find organizations. | [optional] |

### Return type

[**OrganizationsPage**](OrganizationsPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **400**     | Invalid request                                                   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
