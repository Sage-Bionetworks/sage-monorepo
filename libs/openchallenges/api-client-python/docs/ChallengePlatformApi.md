# openchallenges_client.ChallengePlatformApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_challenge_platform**](ChallengePlatformApi.md#get_challenge_platform) | **GET** /challengePlatforms/{challengePlatformName} | Get a challenge platform
[**list_challenge_platforms**](ChallengePlatformApi.md#list_challenge_platforms) | **GET** /challengePlatforms | List challenge platforms


# **get_challenge_platform**
> ChallengePlatform get_challenge_platform(challenge_platform_name)

Get a challenge platform

Returns the challenge platform specified

### Example


```python
import openchallenges_client
from openchallenges_client.models.challenge_platform import ChallengePlatform
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
    api_instance = openchallenges_client.ChallengePlatformApi(api_client)
    challenge_platform_name = 'challenge_platform_name_example' # str | The unique identifier of the challenge platform.

    try:
        # Get a challenge platform
        api_response = api_instance.get_challenge_platform(challenge_platform_name)
        print("The response of ChallengePlatformApi->get_challenge_platform:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->get_challenge_platform: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **challenge_platform_name** | **str**| The unique identifier of the challenge platform. | 

### Return type

[**ChallengePlatform**](ChallengePlatform.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Success |  -  |
**404** | The specified resource was not found |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_challenge_platforms**
> ChallengePlatformsPage list_challenge_platforms(challenge_platform_search_query=challenge_platform_search_query)

List challenge platforms

List challenge platforms

### Example


```python
import openchallenges_client
from openchallenges_client.models.challenge_platform_search_query import ChallengePlatformSearchQuery
from openchallenges_client.models.challenge_platforms_page import ChallengePlatformsPage
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
    api_instance = openchallenges_client.ChallengePlatformApi(api_client)
    challenge_platform_search_query = openchallenges_client.ChallengePlatformSearchQuery() # ChallengePlatformSearchQuery | The search query used to find challenge platforms. (optional)

    try:
        # List challenge platforms
        api_response = api_instance.list_challenge_platforms(challenge_platform_search_query=challenge_platform_search_query)
        print("The response of ChallengePlatformApi->list_challenge_platforms:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengePlatformApi->list_challenge_platforms: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **challenge_platform_search_query** | [**ChallengePlatformSearchQuery**](.md)| The search query used to find challenge platforms. | [optional] 

### Return type

[**ChallengePlatformsPage**](ChallengePlatformsPage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Success |  -  |
**400** | Invalid request |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

