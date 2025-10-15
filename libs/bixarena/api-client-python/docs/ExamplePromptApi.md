# bixarena_api_client.ExamplePromptApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                               | HTTP request             | Description          |
| -------------------------------------------------------------------- | ------------------------ | -------------------- |
| [**list_example_prompts**](ExamplePromptApi.md#list_example_prompts) | **GET** /example-prompts | List example prompts |

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

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **400**     | Invalid request parameters                                        | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
