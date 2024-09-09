# openchallenges_client.ChallengeApi

All URIs are relative to _http://localhost/v1_

| Method                                                 | HTTP request                      | Description     |
| ------------------------------------------------------ | --------------------------------- | --------------- |
| [**get_challenge**](ChallengeApi.md#get_challenge)     | **GET** /challenges/{challengeId} | Get a challenge |
| [**list_challenges**](ChallengeApi.md#list_challenges) | **GET** /challenges               | List challenges |

# **get_challenge**

> Challenge get_challenge(challenge_id)

Get a challenge

Returns the challenge specified

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.challenge import Challenge
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
    api_instance = openchallenges_client.ChallengeApi(api_client)
    challenge_id = 56 # int | The unique identifier of the challenge.

    try:
        # Get a challenge
        api_response = api_instance.get_challenge(challenge_id)
        print("The response of ChallengeApi->get_challenge:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeApi->get_challenge: %s\n" % e)
```

### Parameters

| Name             | Type    | Description                             | Notes |
| ---------------- | ------- | --------------------------------------- | ----- |
| **challenge_id** | **int** | The unique identifier of the challenge. |

### Return type

[**Challenge**](Challenge.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | A challenge                                                       | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_challenges**

> ChallengesPage list_challenges(challenge_search_query=challenge_search_query)

List challenges

List challenges

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.challenge_search_query import ChallengeSearchQuery
from openchallenges_client.models.challenges_page import ChallengesPage
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
    api_instance = openchallenges_client.ChallengeApi(api_client)
    challenge_search_query = openchallenges_client.ChallengeSearchQuery() # ChallengeSearchQuery | The search query used to find challenges. (optional)

    try:
        # List challenges
        api_response = api_instance.list_challenges(challenge_search_query=challenge_search_query)
        print("The response of ChallengeApi->list_challenges:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeApi->list_challenges: %s\n" % e)
```

### Parameters

| Name                       | Type                            | Description                               | Notes      |
| -------------------------- | ------------------------------- | ----------------------------------------- | ---------- |
| **challenge_search_query** | [**ChallengeSearchQuery**](.md) | The search query used to find challenges. | [optional] |

### Return type

[**ChallengesPage**](ChallengesPage.md)

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
