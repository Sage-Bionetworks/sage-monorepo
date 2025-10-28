# bixarena_api_client.StatsApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                               | HTTP request   | Description                    |
| ---------------------------------------------------- | -------------- | ------------------------------ |
| [**get_public_stats**](StatsApi.md#get_public_stats) | **GET** /stats | Get public platform statistics |

# **get_public_stats**

> PublicStats get_public_stats()

Get public platform statistics

Retrieve publicly accessible statistics about the BixArena platform.
This endpoint can be accessed without authentication and is designed
for use on the home page to showcase platform activity.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.public_stats import PublicStats
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
    api_instance = bixarena_api_client.StatsApi(api_client)

    try:
        # Get public platform statistics
        api_response = api_instance.get_public_stats()
        print("The response of StatsApi->get_public_stats:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling StatsApi->get_public_stats: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**PublicStats**](PublicStats.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Public statistics retrieved successfully                          | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
