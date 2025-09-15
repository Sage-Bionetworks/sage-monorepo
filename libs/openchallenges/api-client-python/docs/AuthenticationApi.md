# openchallenges_api_client_python.AuthenticationApi

All URIs are relative to *https://openchallenges.io/api/v1*

| Method                                                              | HTTP request          | Description         |
| ------------------------------------------------------------------- | --------------------- | ------------------- |
| [**get_user_profile**](AuthenticationApi.md#get_user_profile)       | **GET** /auth/profile | Get user profile    |
| [**update_user_profile**](AuthenticationApi.md#update_user_profile) | **PUT** /auth/profile | Update user profile |

# **get_user_profile**

> UserProfile get_user_profile()

Get user profile

Get the authenticated user's profile information

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.user_profile import UserProfile
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.AuthenticationApi(api_client)

    try:
        # Get user profile
        api_response = api_instance.get_user_profile()
        print("The response of AuthenticationApi->get_user_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationApi->get_user_profile: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**UserProfile**](UserProfile.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | User profile information                                          | -                |
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_user_profile**

> UserProfile update_user_profile(update_user_profile_request)

Update user profile

Update the authenticated user's profile information

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.update_user_profile_request import UpdateUserProfileRequest
from openchallenges_api_client_python.models.user_profile import UserProfile
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.AuthenticationApi(api_client)
    update_user_profile_request = openchallenges_api_client_python.UpdateUserProfileRequest() # UpdateUserProfileRequest |

    try:
        # Update user profile
        api_response = api_instance.update_user_profile(update_user_profile_request)
        print("The response of AuthenticationApi->update_user_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationApi->update_user_profile: %s\n" % e)
```

### Parameters

| Name                            | Type                                                        | Description | Notes |
| ------------------------------- | ----------------------------------------------------------- | ----------- | ----- |
| **update_user_profile_request** | [**UpdateUserProfileRequest**](UpdateUserProfileRequest.md) |             |

### Return type

[**UserProfile**](UserProfile.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | User profile updated successfully                                 | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
