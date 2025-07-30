# openchallenges_api_client_python.APIKeyApi

All URIs are relative to *https://openchallenges.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_api_key**](APIKeyApi.md#create_api_key) | **POST** /auth/api-keys | Create API key
[**delete_api_key**](APIKeyApi.md#delete_api_key) | **DELETE** /auth/api-keys/{keyId} | Delete API key
[**list_api_keys**](APIKeyApi.md#list_api_keys) | **GET** /auth/api-keys | List API keys


# **create_api_key**
> CreateApiKeyResponse create_api_key(create_api_key_request)

Create API key

Generate a new API key for the authenticated user

### Example

* Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.create_api_key_request import CreateApiKeyRequest
from openchallenges_api_client_python.models.create_api_key_response import CreateApiKeyResponse
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

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.APIKeyApi(api_client)
    create_api_key_request = openchallenges_api_client_python.CreateApiKeyRequest() # CreateApiKeyRequest | 

    try:
        # Create API key
        api_response = api_instance.create_api_key(create_api_key_request)
        print("The response of APIKeyApi->create_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling APIKeyApi->create_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **create_api_key_request** | [**CreateApiKeyRequest**](CreateApiKeyRequest.md)|  | 

### Return type

[**CreateApiKeyResponse**](CreateApiKeyResponse.md)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | API key created successfully |  -  |
**400** | Invalid request |  -  |
**401** | Unauthorized |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_api_key**
> delete_api_key(key_id)

Delete API key

Revoke an API key

### Example

* Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
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

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.APIKeyApi(api_client)
    key_id = 'key_id_example' # str | The API key ID to delete

    try:
        # Delete API key
        api_instance.delete_api_key(key_id)
    except Exception as e:
        print("Exception when calling APIKeyApi->delete_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key_id** | **str**| The API key ID to delete | 

### Return type

void (empty response body)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | API key deleted successfully |  -  |
**401** | Unauthorized |  -  |
**404** | The specified resource was not found |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_api_keys**
> List[ApiKey] list_api_keys()

List API keys

Get all API keys for the authenticated user

### Example

* Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.api_key import ApiKey
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

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.APIKeyApi(api_client)

    try:
        # List API keys
        api_response = api_instance.list_api_keys()
        print("The response of APIKeyApi->list_api_keys:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling APIKeyApi->list_api_keys: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ApiKey]**](ApiKey.md)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | List of API keys |  -  |
**401** | Unauthorized |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

