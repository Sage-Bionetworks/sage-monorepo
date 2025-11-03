# bixarena_api_client.ModelApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                   | HTTP request                      | Description          |
| -------------------------------------------------------- | --------------------------------- | -------------------- |
| [**create_model_error**](ModelApi.md#create_model_error) | **POST** /models/{modelId}/errors | Report a model error |
| [**list_models**](ModelApi.md#list_models)               | **GET** /models                   | List models          |

# **create_model_error**

> ModelError create_model_error(model_id, model_error_create_request)

Report a model error

Report an error that occurred during model interaction.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.model_error import ModelError
from bixarena_api_client.models.model_error_create_request import ModelErrorCreateRequest
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
    api_instance = bixarena_api_client.ModelApi(api_client)
    model_id = 'model_456' # str | The unique identifier of a model
    model_error_create_request = {"errorCode":429,"errorMessage":"Rate limit exceeded","battleId":"123e4567-e89b-12d3-a456-426614174000","roundId":"123e4567-e89b-12d3-a456-426614174001"} # ModelErrorCreateRequest |

    try:
        # Report a model error
        api_response = api_instance.create_model_error(model_id, model_error_create_request)
        print("The response of ModelApi->create_model_error:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ModelApi->create_model_error: %s\n" % e)
```

### Parameters

| Name                           | Type                                                      | Description                      | Notes |
| ------------------------------ | --------------------------------------------------------- | -------------------------------- | ----- |
| **model_id**                   | **str**                                                   | The unique identifier of a model |
| **model_error_create_request** | [**ModelErrorCreateRequest**](ModelErrorCreateRequest.md) |                                  |

### Return type

[**ModelError**](ModelError.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | Model error reported successfully                                                                 | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_models**

> ModelPage list_models(model_search_query=model_search_query)

List models

Get a paginated list of models with optional filters (e.g., active)

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.model_page import ModelPage
from bixarena_api_client.models.model_search_query import ModelSearchQuery
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
    api_instance = bixarena_api_client.ModelApi(api_client)
    model_search_query = bixarena_api_client.ModelSearchQuery() # ModelSearchQuery | The search query used to find and filter models. (optional)

    try:
        # List models
        api_response = api_instance.list_models(model_search_query=model_search_query)
        print("The response of ModelApi->list_models:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ModelApi->list_models: %s\n" % e)
```

### Parameters

| Name                   | Type                        | Description                                      | Notes      |
| ---------------------- | --------------------------- | ------------------------------------------------ | ---------- |
| **model_search_query** | [**ModelSearchQuery**](.md) | The search query used to find and filter models. | [optional] |

### Return type

[**ModelPage**](ModelPage.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
