# openchallenges_client.ChallengeInputDataTypeApi

All URIs are relative to _http://localhost/v1_

| Method                                                                                              | HTTP request                     | Description                     |
| --------------------------------------------------------------------------------------------------- | -------------------------------- | ------------------------------- |
| [**list_challenge_input_data_types**](ChallengeInputDataTypeApi.md#list_challenge_input_data_types) | **GET** /challengeInputDataTypes | List challenge input data types |

# **list_challenge_input_data_types**

> ChallengeInputDataTypesPage list_challenge_input_data_types(challenge_input_data_type_search_query=challenge_input_data_type_search_query)

List challenge input data types

List challenge input data types

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.challenge_input_data_type_search_query import ChallengeInputDataTypeSearchQuery
from openchallenges_client.models.challenge_input_data_types_page import ChallengeInputDataTypesPage
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.ChallengeInputDataTypeApi(api_client)
    challenge_input_data_type_search_query = openchallenges_client.ChallengeInputDataTypeSearchQuery() # ChallengeInputDataTypeSearchQuery | The search query used to find challenge input data types. (optional)

    try:
        # List challenge input data types
        api_response = api_instance.list_challenge_input_data_types(challenge_input_data_type_search_query=challenge_input_data_type_search_query)
        print("The response of ChallengeInputDataTypeApi->list_challenge_input_data_types:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeInputDataTypeApi->list_challenge_input_data_types: %s\n" % e)
```

### Parameters

| Name                                       | Type                                         | Description                                               | Notes      |
| ------------------------------------------ | -------------------------------------------- | --------------------------------------------------------- | ---------- |
| **challenge_input_data_type_search_query** | [**ChallengeInputDataTypeSearchQuery**](.md) | The search query used to find challenge input data types. | [optional] |

### Return type

[**ChallengeInputDataTypesPage**](ChallengeInputDataTypesPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **400**     | Invalid request                                                   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
