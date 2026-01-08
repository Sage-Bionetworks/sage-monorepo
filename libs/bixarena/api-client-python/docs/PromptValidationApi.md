# bixarena_api_client.PromptValidationApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                        | HTTP request             | Description                |
| ------------------------------------------------------------- | ------------------------ | -------------------------- |
| [**validate_prompt**](PromptValidationApi.md#validate_prompt) | **GET** /validate-prompt | Validate biomedical prompt |

# **validate_prompt**

> PromptValidation validate_prompt(prompt)

Validate biomedical prompt

Validates whether a prompt is biomedically related and returns a confidence score (requires authentication)

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.prompt_validation import PromptValidation
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
    api_instance = bixarena_api_client.PromptValidationApi(api_client)
    prompt = 'prompt_example' # str | The prompt text to validate

    try:
        # Validate biomedical prompt
        api_response = api_instance.validate_prompt(prompt)
        print("The response of PromptValidationApi->validate_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptValidationApi->validate_prompt: %s\n" % e)
```

### Parameters

| Name       | Type    | Description                 | Notes |
| ---------- | ------- | --------------------------- | ----- |
| **prompt** | **str** | The prompt text to validate |

### Return type

[**PromptValidation**](PromptValidation.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
