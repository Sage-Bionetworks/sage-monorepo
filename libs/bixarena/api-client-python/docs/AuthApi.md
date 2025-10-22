# bixarena_api_client.AuthApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                        | HTTP request                   | Description                                |
| --------------------------------------------- | ------------------------------ | ------------------------------------------ |
| [**callback**](AuthApi.md#callback)           | **GET** /auth/callback         | OIDC redirect callback                     |
| [**get_jwks**](AuthApi.md#get_jwks)           | **GET** /.well-known/jwks.json | JSON Web Key Set                           |
| [**get_user_info**](AuthApi.md#get_user_info) | **GET** /userinfo              | Get current user profile                   |
| [**login**](AuthApi.md#login)                 | **GET** /auth/login            | Start Synapse OIDC authorization code flow |
| [**logout**](AuthApi.md#logout)               | **POST** /auth/logout          | Logout current session                     |
| [**token**](AuthApi.md#token)                 | **POST** /oauth2/token         | Mint short-lived internal JWT              |

# **callback**

> Callback200Response callback(code, state)

OIDC redirect callback

Handles redirect from Synapse, validates state and nonce, establishes authenticated session.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.callback200_response import Callback200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)
    code = 'code_example' # str |
    state = 'state_example' # str |

    try:
        # OIDC redirect callback
        api_response = api_instance.callback(code, state)
        print("The response of AuthApi->callback:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->callback: %s\n" % e)
```

### Parameters

| Name      | Type    | Description | Notes |
| --------- | ------- | ----------- | ----- |
| **code**  | **str** |             |
| **state** | **str** |             |

### Return type

[**Callback200Response**](Callback200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description               | Response headers |
| ----------- | ------------------------- | ---------------- |
| **200**     | Authentication successful | -                |
| **400**     | Invalid request           | -                |
| **401**     | Unauthorized              | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_jwks**

> GetJwks200Response get_jwks()

JSON Web Key Set

Returns the public keys used to verify internally issued JWTs.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.get_jwks200_response import GetJwks200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # JSON Web Key Set
        api_response = api_instance.get_jwks()
        print("The response of AuthApi->get_jwks:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->get_jwks: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**GetJwks200Response**](GetJwks200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description     | Response headers |
| ----------- | --------------- | ---------------- |
| **200**     | JWKS document   | -                |
| **400**     | Invalid request | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_user_info**

> UserInfo get_user_info()

Get current user profile

Returns the authenticated user's profile information.
This is an OIDC-compliant UserInfo endpoint that provides details about the currently authenticated user.

Requires a valid JWT obtained via the `/token` endpoint or an active session.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.user_info import UserInfo
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Get current user profile
        api_response = api_instance.get_user_info()
        print("The response of AuthApi->get_user_info:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->get_user_info: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**UserInfo**](UserInfo.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description              | Response headers |
| ----------- | ------------------------ | ---------------- |
| **200**     | User profile information | -                |
| **401**     | Unauthorized             | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **login**

> login()

Start Synapse OIDC authorization code flow

Initiates the OIDC login by redirecting the user to Synapse with state and nonce.

### Example

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Start Synapse OIDC authorization code flow
        api_instance.login()
    except Exception as e:
        print("Exception when calling AuthApi->login: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description                                               | Response headers |
| ----------- | --------------------------------------------------------- | ---------------- |
| **204**     | Flow started (no content; clients should follow redirect) | -                |
| **302**     | Redirect to Synapse login                                 | -                |
| **400**     | Invalid request                                           | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logout**

> logout()

Logout current session

Invalidate the current authenticated session. Requires an active session.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Logout current session
        api_instance.logout()
    except Exception as e:
        print("Exception when calling AuthApi->logout: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description             | Response headers |
| ----------- | ----------------------- | ---------------- |
| **204**     | Logged out successfully | -                |
| **401**     | Unauthorized            | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **token**

> Token200Response token()

Mint short-lived internal JWT

Exchanges an authenticated session (cookie) for an internal JWT (OAuth2-style endpoint).

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.token200_response import Token200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Mint short-lived internal JWT
        api_response = api_instance.token()
        print("The response of AuthApi->token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->token: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Token200Response**](Token200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description           | Response headers |
| ----------- | --------------------- | ---------------- |
| **200**     | Access token response | -                |
| **401**     | Unauthorized          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
