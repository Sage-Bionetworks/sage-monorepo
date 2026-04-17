# bixarena_api_client.ExamplePromptApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                                                                             | HTTP request                                                           | Description                                 |
| ------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------- | ------------------------------------------- |
| [**create_example_prompt**](ExamplePromptApi.md#create_example_prompt)                                             | **POST** /example-prompts                                              | Create an example prompt                    |
| [**create_example_prompt_categorization**](ExamplePromptApi.md#create_example_prompt_categorization)               | **POST** /example-prompts/{examplePromptId}/categorizations            | Create an example prompt categorization     |
| [**delete_example_prompt**](ExamplePromptApi.md#delete_example_prompt)                                             | **DELETE** /example-prompts/{examplePromptId}                          | Delete an example prompt                    |
| [**get_example_prompt**](ExamplePromptApi.md#get_example_prompt)                                                   | **GET** /example-prompts/{examplePromptId}                             | Get an example prompt                       |
| [**list_example_prompt_categorizations**](ExamplePromptApi.md#list_example_prompt_categorizations)                 | **GET** /example-prompts/{examplePromptId}/categorizations             | List example prompt categorizations         |
| [**list_example_prompts**](ExamplePromptApi.md#list_example_prompts)                                               | **GET** /example-prompts                                               | List example prompts                        |
| [**run_example_prompt_categorization**](ExamplePromptApi.md#run_example_prompt_categorization)                     | **POST** /example-prompts/{examplePromptId}/categorizations/run        | Run an automated categorization             |
| [**set_effective_example_prompt_categorization**](ExamplePromptApi.md#set_effective_example_prompt_categorization) | **PATCH** /example-prompts/{examplePromptId}/categorizations/effective | Set effective example prompt categorization |
| [**update_example_prompt**](ExamplePromptApi.md#update_example_prompt)                                             | **PATCH** /example-prompts/{examplePromptId}                           | Update an example prompt                    |

# **create_example_prompt**

> ExamplePrompt create_example_prompt(example_prompt_create_request)

Create an example prompt

Create a new example prompt. Newly created prompts are inactive; a reviewer publishes them via PATCH. AI auto-categorization runs asynchronously after creation.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt import ExamplePrompt
from bixarena_api_client.models.example_prompt_create_request import ExamplePromptCreateRequest
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_create_request = bixarena_api_client.ExamplePromptCreateRequest() # ExamplePromptCreateRequest |

    try:
        # Create an example prompt
        api_response = api_instance.create_example_prompt(example_prompt_create_request)
        print("The response of ExamplePromptApi->create_example_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->create_example_prompt: %s\n" % e)
```

### Parameters

| Name                              | Type                                                            | Description | Notes |
| --------------------------------- | --------------------------------------------------------------- | ----------- | ----- |
| **example_prompt_create_request** | [**ExamplePromptCreateRequest**](ExamplePromptCreateRequest.md) |             |

### Return type

[**ExamplePrompt**](ExamplePrompt.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Example prompt created successfully                               | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_example_prompt_categorization**

> ExamplePromptCategorizationResponse create_example_prompt_categorization(example_prompt_id, example_prompt_categorization_create_request)

Create an example prompt categorization

Manually categorize an example prompt. The created categorization is automatically set as the effective categorization.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt_categorization_create_request import ExamplePromptCategorizationCreateRequest
from bixarena_api_client.models.example_prompt_categorization_response import ExamplePromptCategorizationResponse
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt
    example_prompt_categorization_create_request = bixarena_api_client.ExamplePromptCategorizationCreateRequest() # ExamplePromptCategorizationCreateRequest |

    try:
        # Create an example prompt categorization
        api_response = api_instance.create_example_prompt_categorization(example_prompt_id, example_prompt_categorization_create_request)
        print("The response of ExamplePromptApi->create_example_prompt_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->create_example_prompt_categorization: %s\n" % e)
```

### Parameters

| Name                                             | Type                                                                                        | Description                                | Notes |
| ------------------------------------------------ | ------------------------------------------------------------------------------------------- | ------------------------------------------ | ----- |
| **example_prompt_id**                            | **str**                                                                                     | The unique identifier of an example prompt |
| **example_prompt_categorization_create_request** | [**ExamplePromptCategorizationCreateRequest**](ExamplePromptCategorizationCreateRequest.md) |                                            |

### Return type

[**ExamplePromptCategorizationResponse**](ExamplePromptCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Example prompt categorization created successfully                | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_example_prompt**

> delete_example_prompt(example_prompt_id)

Delete an example prompt

Delete an example prompt.

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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt

    try:
        # Delete an example prompt
        api_instance.delete_example_prompt(example_prompt_id)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->delete_example_prompt: %s\n" % e)
```

### Parameters

| Name                  | Type    | Description                                | Notes |
| --------------------- | ------- | ------------------------------------------ | ----- |
| **example_prompt_id** | **str** | The unique identifier of an example prompt |

### Return type

void (empty response body)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **204**     | Example prompt deleted successfully                               | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_example_prompt**

> ExamplePrompt get_example_prompt(example_prompt_id)

Get an example prompt

Get an example prompt by ID.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt import ExamplePrompt
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt

    try:
        # Get an example prompt
        api_response = api_instance.get_example_prompt(example_prompt_id)
        print("The response of ExamplePromptApi->get_example_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->get_example_prompt: %s\n" % e)
```

### Parameters

| Name                  | Type    | Description                                | Notes |
| --------------------- | ------- | ------------------------------------------ | ----- |
| **example_prompt_id** | **str** | The unique identifier of an example prompt |

### Return type

[**ExamplePrompt**](ExamplePrompt.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_example_prompt_categorizations**

> List[ExamplePromptCategorizationResponse] list_example_prompt_categorizations(example_prompt_id)

List example prompt categorizations

Get all categorizations for an example prompt.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt_categorization_response import ExamplePromptCategorizationResponse
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt

    try:
        # List example prompt categorizations
        api_response = api_instance.list_example_prompt_categorizations(example_prompt_id)
        print("The response of ExamplePromptApi->list_example_prompt_categorizations:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->list_example_prompt_categorizations: %s\n" % e)
```

### Parameters

| Name                  | Type    | Description                                | Notes |
| --------------------- | ------- | ------------------------------------------ | ----- |
| **example_prompt_id** | **str** | The unique identifier of an example prompt |

### Return type

[**List[ExamplePromptCategorizationResponse]**](ExamplePromptCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | List of example prompt categorizations                            | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_example_prompts**

> ExamplePromptPage list_example_prompts(example_prompt_search_query=example_prompt_search_query)

List example prompts

Get a list of example prompts with comprehensive filtering options

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt_page import ExamplePromptPage
from bixarena_api_client.models.example_prompt_search_query import ExamplePromptSearchQuery
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_search_query = bixarena_api_client.ExamplePromptSearchQuery() # ExamplePromptSearchQuery | The search query used to find and filter example prompts. (optional)

    try:
        # List example prompts
        api_response = api_instance.list_example_prompts(example_prompt_search_query=example_prompt_search_query)
        print("The response of ExamplePromptApi->list_example_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->list_example_prompts: %s\n" % e)
```

### Parameters

| Name                            | Type                                | Description                                               | Notes      |
| ------------------------------- | ----------------------------------- | --------------------------------------------------------- | ---------- |
| **example_prompt_search_query** | [**ExamplePromptSearchQuery**](.md) | The search query used to find and filter example prompts. | [optional] |

### Return type

[**ExamplePromptPage**](ExamplePromptPage.md)

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

# **run_example_prompt_categorization**

> ExamplePromptCategorizationResponse run_example_prompt_categorization(example_prompt_id)

Run an automated categorization

Run an automated AI categorization against an example prompt. Returns 201 with the persisted row when the AI matched at least one category, or 204 when the AI could not match any category from the taxonomy (no row is persisted in that case).

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt_categorization_response import ExamplePromptCategorizationResponse
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt

    try:
        # Run an automated categorization
        api_response = api_instance.run_example_prompt_categorization(example_prompt_id)
        print("The response of ExamplePromptApi->run_example_prompt_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->run_example_prompt_categorization: %s\n" % e)
```

### Parameters

| Name                  | Type    | Description                                | Notes |
| --------------------- | ------- | ------------------------------------------ | ----- |
| **example_prompt_id** | **str** | The unique identifier of an example prompt |

### Return type

[**ExamplePromptCategorizationResponse**](ExamplePromptCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                        | Response headers |
| ----------- | ------------------------------------------------------------------ | ---------------- |
| **201**     | Categorization completed and persisted successfully                | -                |
| **204**     | Categorization run completed but the AI did not match any category | -                |
| **400**     | Invalid request                                                    | -                |
| **401**     | Unauthorized                                                       | -                |
| **403**     | The user does not have the permission to perform this action       | -                |
| **404**     | The specified resource was not found                               | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error  | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_effective_example_prompt_categorization**

> ExamplePrompt set_effective_example_prompt_categorization(example_prompt_id, set_effective_categorization_request)

Set effective example prompt categorization

Set or clear the effective categorization for an example prompt by pointing at a row from history. Pass null to clear.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt import ExamplePrompt
from bixarena_api_client.models.set_effective_categorization_request import SetEffectiveCategorizationRequest
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt
    set_effective_categorization_request = bixarena_api_client.SetEffectiveCategorizationRequest() # SetEffectiveCategorizationRequest |

    try:
        # Set effective example prompt categorization
        api_response = api_instance.set_effective_example_prompt_categorization(example_prompt_id, set_effective_categorization_request)
        print("The response of ExamplePromptApi->set_effective_example_prompt_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->set_effective_example_prompt_categorization: %s\n" % e)
```

### Parameters

| Name                                     | Type                                                                          | Description                                | Notes |
| ---------------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------ | ----- |
| **example_prompt_id**                    | **str**                                                                       | The unique identifier of an example prompt |
| **set_effective_categorization_request** | [**SetEffectiveCategorizationRequest**](SetEffectiveCategorizationRequest.md) |                                            |

### Return type

[**ExamplePrompt**](ExamplePrompt.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Effective categorization updated successfully                     | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_example_prompt**

> ExamplePrompt update_example_prompt(example_prompt_id, example_prompt_update_request)

Update an example prompt

Partially update an example prompt. Only fields present in the request body are modified. If the question text changes, AI auto-categorization runs asynchronously.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.example_prompt import ExamplePrompt
from bixarena_api_client.models.example_prompt_update_request import ExamplePromptUpdateRequest
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
    api_instance = bixarena_api_client.ExamplePromptApi(api_client)
    example_prompt_id = '123e4567-e89b-12d3-a456-426614174000' # str | The unique identifier of an example prompt
    example_prompt_update_request = bixarena_api_client.ExamplePromptUpdateRequest() # ExamplePromptUpdateRequest |

    try:
        # Update an example prompt
        api_response = api_instance.update_example_prompt(example_prompt_id, example_prompt_update_request)
        print("The response of ExamplePromptApi->update_example_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExamplePromptApi->update_example_prompt: %s\n" % e)
```

### Parameters

| Name                              | Type                                                            | Description                                | Notes |
| --------------------------------- | --------------------------------------------------------------- | ------------------------------------------ | ----- |
| **example_prompt_id**             | **str**                                                         | The unique identifier of an example prompt |
| **example_prompt_update_request** | [**ExamplePromptUpdateRequest**](ExamplePromptUpdateRequest.md) |                                            |

### Return type

[**ExamplePrompt**](ExamplePrompt.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Example prompt updated successfully                                                               | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
