# openchallenges_api_client_python.AuthenticationApi

All URIs are relative to *https://openchallenges.io/api/v1*

| Method                                  | HTTP request         | Description |
| --------------------------------------- | -------------------- | ----------- |
| [**login**](AuthenticationApi.md#login) | **POST** /auth/login | User login  |

# **login**

> LoginResponse login(login_request)

User login

Authenticate user and return JWT token

### Example

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.login_request import LoginRequest
from openchallenges_api_client_python.models.login_response import LoginResponse
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.AuthenticationApi(api_client)
    login_request = openchallenges_api_client_python.LoginRequest() # LoginRequest |

    try:
        # User login
        api_response = api_instance.login(login_request)
        print("The response of AuthenticationApi->login:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationApi->login: %s\n" % e)
```

### Parameters

| Name              | Type                                | Description | Notes |
| ----------------- | ----------------------------------- | ----------- | ----- |
| **login_request** | [**LoginRequest**](LoginRequest.md) |             |

### Return type

[**LoginResponse**](LoginResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Login successful                                                  | -                |
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
