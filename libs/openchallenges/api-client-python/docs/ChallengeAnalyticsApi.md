# openchallenges_api_client_python.ChallengeAnalyticsApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_challenges_per_year**](ChallengeAnalyticsApi.md#get_challenges_per_year) | **GET** /challengeAnalytics/challengesPerYear | Get the number of challenges tracked per year


# **get_challenges_per_year**
> ChallengesPerYear get_challenges_per_year()

Get the number of challenges tracked per year

Returns the number of challenges tracked per year

### Example


```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenges_per_year import ChallengesPerYear
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeAnalyticsApi(api_client)

    try:
        # Get the number of challenges tracked per year
        api_response = api_instance.get_challenges_per_year()
        print("The response of ChallengeAnalyticsApi->get_challenges_per_year:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeAnalyticsApi->get_challenges_per_year: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**ChallengesPerYear**](ChallengesPerYear.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | An object |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

