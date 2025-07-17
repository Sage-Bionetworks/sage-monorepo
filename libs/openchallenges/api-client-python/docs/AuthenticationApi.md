# openchallenges_api_client_python.AuthenticationApi

All URIs are relative to _http://localhost/v1_

| Method                                                        | HTTP request            | Description      |
| ------------------------------------------------------------- | ----------------------- | ---------------- |
| [**login**](AuthenticationApi.md#login)                       | **POST** /auth/login    | User login       |
| [**validate_api_key**](AuthenticationApi.md#validate_api_key) | **POST** /auth/validate | Validate API key |

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

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
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

# **validate_api_key**

> ValidateApiKeyResponse validate_api_key(validate_api_key_request)

Validate API key

Internal endpoint to validate API keys (used by other services)

### Example

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.validate_api_key_request import ValidateApiKeyRequest
from openchallenges_api_client_python.models.validate_api_key_response import ValidateApiKeyResponse
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
    api_instance = openchallenges_api_client_python.AuthenticationApi(api_client)
    validate_api_key_request = openchallenges_api_client_python.ValidateApiKeyRequest() # ValidateApiKeyRequest |

    try:
        # Validate API key
        api_response = api_instance.validate_api_key(validate_api_key_request)
        print("The response of AuthenticationApi->validate_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationApi->validate_api_key: %s\n" % e)
```

### Parameters

| Name                         | Type                                                  | Description | Notes |
| ---------------------------- | ----------------------------------------------------- | ----------- | ----- |
| **validate_api_key_request** | [**ValidateApiKeyRequest**](ValidateApiKeyRequest.md) |             |

### Return type

[**ValidateApiKeyResponse**](ValidateApiKeyResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | API key is valid                                                  | -                |
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
