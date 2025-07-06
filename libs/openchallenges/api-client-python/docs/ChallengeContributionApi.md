# openchallenges.ChallengeContributionApi

All URIs are relative to _http://localhost/v1_

| Method                                                                                       | HTTP request                                    | Description                  |
| -------------------------------------------------------------------------------------------- | ----------------------------------------------- | ---------------------------- |
| [**list_challenge_contributions**](ChallengeContributionApi.md#list_challenge_contributions) | **GET** /challenges/{challengeId}/contributions | List challenge contributions |

# **list_challenge_contributions**

> ChallengeContributionsPage list_challenge_contributions(challenge_id)

List challenge contributions

List challenge contributions

### Example

```python
import openchallenges
from openchallenges.models.challenge_contributions_page import ChallengeContributionsPage
from openchallenges.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges.ChallengeContributionApi(api_client)
    challenge_id = 56 # int | The unique identifier of the challenge.

    try:
        # List challenge contributions
        api_response = api_instance.list_challenge_contributions(challenge_id)
        print("The response of ChallengeContributionApi->list_challenge_contributions:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeContributionApi->list_challenge_contributions: %s\n" % e)
```

### Parameters

| Name             | Type    | Description                             | Notes |
| ---------------- | ------- | --------------------------------------- | ----- |
| **challenge_id** | **int** | The unique identifier of the challenge. |

### Return type

[**ChallengeContributionsPage**](ChallengeContributionsPage.md)

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
