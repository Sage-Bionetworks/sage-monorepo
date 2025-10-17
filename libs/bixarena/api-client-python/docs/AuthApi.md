# bixarena_api_client.AuthApi

All URIs are relative to _http://localhost/v1_

| Method                                                    | HTTP request                   | Description                                |
| --------------------------------------------------------- | ------------------------------ | ------------------------------------------ |
| [**get_jwks**](AuthApi.md#get_jwks)                       | **GET** /.well-known/jwks.json | JSON Web Key Set                           |
| [**logout**](AuthApi.md#logout)                           | **POST** /auth/logout          | Logout current session                     |
| [**mint_internal_token**](AuthApi.md#mint_internal_token) | **POST** /token                | Mint short-lived internal JWT              |
| [**oidc_callback**](AuthApi.md#oidc_callback)             | **GET** /auth/oidc/callback    | OIDC redirect callback                     |
| [**start_oidc**](AuthApi.md#start_oidc)                   | **GET** /auth/oidc/start       | Start Synapse OIDC authorization code flow |

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

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost/v1"
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

| Status code | Description                | Response headers |
| ----------- | -------------------------- | ---------------- |
| **200**     | JWKS document              | -                |
| **400**     | Invalid request parameters | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logout**

> logout()

Logout current session

Invalidate the current authenticated session.

### Example

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost/v1"
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

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details

| Status code | Description             | Response headers |
| ----------- | ----------------------- | ---------------- |
| **204**     | Logged out (idempotent) | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **mint_internal_token**

> MintInternalToken200Response mint_internal_token()

Mint short-lived internal JWT

Exchanges an authenticated session (cookie) for an internal JWT.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.mint_internal_token200_response import MintInternalToken200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Mint short-lived internal JWT
        api_response = api_instance.mint_internal_token()
        print("The response of AuthApi->mint_internal_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->mint_internal_token: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**MintInternalToken200Response**](MintInternalToken200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details

| Status code | Description           | Response headers |
| ----------- | --------------------- | ---------------- |
| **200**     | Access token response | -                |
| **401**     | Unauthorized          | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **oidc_callback**

> OidcCallback200Response oidc_callback(code, state)

OIDC redirect callback

Handles redirect from Synapse, validates state and nonce, establishes authenticated session.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.oidc_callback200_response import OidcCallback200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)
    code = 'code_example' # str |
    state = 'state_example' # str |

    try:
        # OIDC redirect callback
        api_response = api_instance.oidc_callback(code, state)
        print("The response of AuthApi->oidc_callback:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthApi->oidc_callback: %s\n" % e)
```

### Parameters

| Name      | Type    | Description | Notes |
| --------- | ------- | ----------- | ----- |
| **code**  | **str** |             |
| **state** | **str** |             |

### Return type

[**OidcCallback200Response**](OidcCallback200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                | Response headers |
| ----------- | -------------------------- | ---------------- |
| **200**     | Authentication successful  | -                |
| **400**     | Invalid request parameters | -                |
| **401**     | Unauthorized               | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **start_oidc**

> start_oidc()

Start Synapse OIDC authorization code flow

Initiates the OIDC login by redirecting the user to Synapse with state and nonce.

### Example

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AuthApi(api_client)

    try:
        # Start Synapse OIDC authorization code flow
        api_instance.start_oidc()
    except Exception as e:
        print("Exception when calling AuthApi->start_oidc: %s\n" % e)
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
| **400**     | Invalid request parameters                                | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
