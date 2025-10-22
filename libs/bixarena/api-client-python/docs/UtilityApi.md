# bixarena_api_client.UtilityApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                         | HTTP request  | Description                  |
| ------------------------------ | ------------- | ---------------------------- |
| [**echo**](UtilityApi.md#echo) | **GET** /echo | Echo authenticated principal |

# **echo**

> Echo200Response echo()

Echo authenticated principal

Returns information about the authenticated user.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.echo200_response import Echo200Response
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
    api_instance = bixarena_api_client.UtilityApi(api_client)

    try:
        # Echo authenticated principal
        api_response = api_instance.echo()
        print("The response of UtilityApi->echo:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UtilityApi->echo: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Echo200Response**](Echo200Response.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description  | Response headers |
| ----------- | ------------ | ---------------- |
| **200**     | Success      | -                |
| **401**     | Unauthorized | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
