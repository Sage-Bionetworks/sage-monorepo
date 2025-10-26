# bixarena_api_client.AdminApi

All URIs are relative to _http://localhost_

| Method                                     | HTTP request         | Description      |
| ------------------------------------------ | -------------------- | ---------------- |
| [**admin_stats**](AdminApi.md#admin_stats) | **GET** /admin/stats | Admin statistics |

# **admin_stats**

> AdminStats200Response admin_stats()

Admin statistics

Administrative operations requiring admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.admin_stats200_response import AdminStats200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "http://localhost"
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
    api_instance = bixarena_api_client.AdminApi(api_client)

    try:
        # Admin statistics
        api_response = api_instance.admin_stats()
        print("The response of AdminApi->admin_stats:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->admin_stats: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**AdminStats200Response**](AdminStats200Response.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                  | Response headers |
| ----------- | ------------------------------------------------------------ | ---------------- |
| **200**     | Success                                                      | -                |
| **401**     | Unauthorized                                                 | -                |
| **403**     | The user does not have the permission to perform this action | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
