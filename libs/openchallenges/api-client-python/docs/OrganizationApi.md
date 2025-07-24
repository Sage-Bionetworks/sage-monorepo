# openchallenges_api_client_python.OrganizationApi

All URIs are relative to _http://localhost/v1_

| Method                                                            | HTTP request                    | Description            |
| ----------------------------------------------------------------- | ------------------------------- | ---------------------- |
| [**delete_organization**](OrganizationApi.md#delete_organization) | **DELETE** /organizations/{org} | Delete an organization |
| [**get_organization**](OrganizationApi.md#get_organization)       | **GET** /organizations/{org}    | Get an organization    |
| [**list_organizations**](OrganizationApi.md#list_organizations)   | **GET** /organizations          | List organizations     |

# **delete_organization**

> delete_organization(org)

Delete an organization

Deletes the organization specified

### Example

- Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.OrganizationApi(api_client)
    org = 'dream' # str | The id or login of the organization.

    try:
        # Delete an organization
        api_instance.delete_organization(org)
    except Exception as e:
        print("Exception when calling OrganizationApi->delete_organization: %s\n" % e)
```

### Parameters

| Name    | Type    | Description                          | Notes |
| ------- | ------- | ------------------------------------ | ----- |
| **org** | **str** | The id or login of the organization. |

### Return type

void (empty response body)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **204**     | Organization successfully deleted                                 | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_organization**

> Organization get_organization(org)

Get an organization

Returns the organization specified

### Example

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.organization import Organization
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.OrganizationApi(api_client)
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
import openchallenges_api_client_python
from openchallenges_api_client_python.models.organization_search_query import OrganizationSearchQuery
from openchallenges_api_client_python.models.organizations_page import OrganizationsPage
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.OrganizationApi(api_client)
    organization_search_query = openchallenges_api_client_python.OrganizationSearchQuery() # OrganizationSearchQuery | The search query used to find organizations. (optional)

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
