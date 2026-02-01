# bixarena_api_client.QuestApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                           | HTTP request                           | Description            |
| ---------------------------------------------------------------- | -------------------------------------- | ---------------------- |
| [**get_quest_contributors**](QuestApi.md#get_quest_contributors) | **GET** /quests/{questId}/contributors | Get quest contributors |

# **get_quest_contributors**

> QuestContributors get_quest_contributors(quest_id, min_battles=min_battles, limit=limit)

Get quest contributors

Get a list of users who have contributed to a specific quest,
ordered by their battle count during the quest period.
Results are calculated in real-time based on completed battles
within the quest's start and end dates.

### Example

```python
import bixarena_api_client
from bixarena_api_client.models.quest_contributors import QuestContributors
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
    api_instance = bixarena_api_client.QuestApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    min_battles = 1 # int | Minimum number of battles required to be listed (optional) (default to 1)
    limit = 100 # int | Maximum number of contributors to return (optional) (default to 100)

    try:
        # Get quest contributors
        api_response = api_instance.get_quest_contributors(quest_id, min_battles=min_battles, limit=limit)
        print("The response of QuestApi->get_quest_contributors:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling QuestApi->get_quest_contributors: %s\n" % e)
```

### Parameters

| Name            | Type    | Description                                     | Notes                       |
| --------------- | ------- | ----------------------------------------------- | --------------------------- |
| **quest_id**    | **str** | Unique identifier for a quest                   |
| **min_battles** | **int** | Minimum number of battles required to be listed | [optional] [default to 1]   |
| **limit**       | **int** | Maximum number of contributors to return        | [optional] [default to 100] |

### Return type

[**QuestContributors**](QuestContributors.md)

### Authorization

No authorization required

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
