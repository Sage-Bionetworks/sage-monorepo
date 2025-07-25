# openchallenges_api_client_python.ChallengePlatformApi

All URIs are relative to _http://localhost/v1_

| Method                                                                             | HTTP request                                          | Description                           |
| ---------------------------------------------------------------------------------- | ----------------------------------------------------- | ------------------------------------- |
| [**create_challenge_platform**](ChallengePlatformApi.md#create_challenge_platform) | **POST** /challenge-platforms                         | Create a challenge platform           |
| [**delete_challenge_platform**](ChallengePlatformApi.md#delete_challenge_platform) | **DELETE** /challenge-platforms/{challengePlatformId} | Delete a challenge platform           |
| [**get_challenge_platform**](ChallengePlatformApi.md#get_challenge_platform)       | **GET** /challenge-platforms/{challengePlatformId}    | Get a challenge platform              |
| [**list_challenge_platforms**](ChallengePlatformApi.md#list_challenge_platforms)   | **GET** /challenge-platforms                          | List challenge platforms              |
| [**update_challenge_platform**](ChallengePlatformApi.md#update_challenge_platform) | **PUT** /challenge-platforms/{challengePlatformId}    | Update an existing challenge platform |

# **create_challenge_platform**

> ChallengePlatform create_challenge_platform(challenge_platform_create_request)

Create a challenge platform

Create a challenge platform with the specified ID

### Example

- Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_platform import ChallengePlatform
from openchallenges_api_client_python.models.challenge_platform_create_request import ChallengePlatformCreateRequest
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
    api_instance = openchallenges_api_client_python.ChallengePlatformApi(api_client)
    challenge_platform_create_request = openchallenges_api_client_python.ChallengePlatformCreateRequest() # ChallengePlatformCreateRequest |

    try:
        # Create a challenge platform
        api_response = api_instance.create_challenge_platform(challenge_platform_create_request)
        print("The response of ChallengePlatformApi->create_challenge_platform:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->create_challenge_platform: %s\n" % e)
```

### Parameters

| Name                                  | Type                                                                    | Description | Notes |
| ------------------------------------- | ----------------------------------------------------------------------- | ----------- | ----- |
| **challenge_platform_create_request** | [**ChallengePlatformCreateRequest**](ChallengePlatformCreateRequest.md) |             |

### Return type

[**ChallengePlatform**](ChallengePlatform.md)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Success                                                           | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_challenge_platform**

> delete_challenge_platform(challenge_platform_id)

Delete a challenge platform

Deletes a challenge platform by its unique ID. This action is irreversible.

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
    api_instance = openchallenges_api_client_python.ChallengePlatformApi(api_client)
    challenge_platform_id = 56 # int | The unique identifier of the challenge platform.

    try:
        # Delete a challenge platform
        api_instance.delete_challenge_platform(challenge_platform_id)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->delete_challenge_platform: %s\n" % e)
```

### Parameters

| Name                      | Type    | Description                                      | Notes |
| ------------------------- | ------- | ------------------------------------------------ | ----- |
| **challenge_platform_id** | **int** | The unique identifier of the challenge platform. |

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
| **204**     | Deletion successful                                               | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_challenge_platform**

> ChallengePlatform get_challenge_platform(challenge_platform_id)

Get a challenge platform

Returns the challenge platform identified by its unique ID

### Example

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_platform import ChallengePlatform
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
    api_instance = openchallenges_api_client_python.ChallengePlatformApi(api_client)
    challenge_platform_id = 56 # int | The unique identifier of the challenge platform.

    try:
        # Get a challenge platform
        api_response = api_instance.get_challenge_platform(challenge_platform_id)
        print("The response of ChallengePlatformApi->get_challenge_platform:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->get_challenge_platform: %s\n" % e)
```

### Parameters

| Name                      | Type    | Description                                      | Notes |
| ------------------------- | ------- | ------------------------------------------------ | ----- |
| **challenge_platform_id** | **int** | The unique identifier of the challenge platform. |

### Return type

[**ChallengePlatform**](ChallengePlatform.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_challenge_platforms**

> ChallengePlatformsPage list_challenge_platforms(challenge_platform_search_query=challenge_platform_search_query)

List challenge platforms

List challenge platforms

### Example

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_platform_search_query import ChallengePlatformSearchQuery
from openchallenges_api_client_python.models.challenge_platforms_page import ChallengePlatformsPage
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
    api_instance = openchallenges_api_client_python.ChallengePlatformApi(api_client)
    challenge_platform_search_query = openchallenges_api_client_python.ChallengePlatformSearchQuery() # ChallengePlatformSearchQuery | The search query used to find challenge platforms. (optional)

    try:
        # List challenge platforms
        api_response = api_instance.list_challenge_platforms(challenge_platform_search_query=challenge_platform_search_query)
        print("The response of ChallengePlatformApi->list_challenge_platforms:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->list_challenge_platforms: %s\n" % e)
```

### Parameters

| Name                                | Type                                    | Description                                        | Notes      |
| ----------------------------------- | --------------------------------------- | -------------------------------------------------- | ---------- |
| **challenge_platform_search_query** | [**ChallengePlatformSearchQuery**](.md) | The search query used to find challenge platforms. | [optional] |

### Return type

[**ChallengePlatformsPage**](ChallengePlatformsPage.md)

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

# **update_challenge_platform**

> ChallengePlatform update_challenge_platform(challenge_platform_id, challenge_platform_update_request)

Update an existing challenge platform

Updates an existing challenge platform.

### Example

- Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_platform import ChallengePlatform
from openchallenges_api_client_python.models.challenge_platform_update_request import ChallengePlatformUpdateRequest
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
    api_instance = openchallenges_api_client_python.ChallengePlatformApi(api_client)
    challenge_platform_id = 56 # int | The unique identifier of the challenge platform.
    challenge_platform_update_request = openchallenges_api_client_python.ChallengePlatformUpdateRequest() # ChallengePlatformUpdateRequest |

    try:
        # Update an existing challenge platform
        api_response = api_instance.update_challenge_platform(challenge_platform_id, challenge_platform_update_request)
        print("The response of ChallengePlatformApi->update_challenge_platform:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->update_challenge_platform: %s\n" % e)
```

### Parameters

| Name                                  | Type                                                                    | Description                                      | Notes |
| ------------------------------------- | ----------------------------------------------------------------------- | ------------------------------------------------ | ----- |
| **challenge_platform_id**             | **int**                                                                 | The unique identifier of the challenge platform. |
| **challenge_platform_update_request** | [**ChallengePlatformUpdateRequest**](ChallengePlatformUpdateRequest.md) |                                                  |

### Return type

[**ChallengePlatform**](ChallengePlatform.md)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Challange platform updated successfully                           | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
