# ChallengePlatformApi

All URIs are relative to _http://localhost/v1_

| Method                                                                       | HTTP request                                        | Description              |
| ---------------------------------------------------------------------------- | --------------------------------------------------- | ------------------------ |
| [**GetChallengePlatform**](ChallengePlatformApi.md#GetChallengePlatform)     | **GET** /challengePlatforms/{challengePlatformName} | Get a challenge platform |
| [**ListChallengePlatforms**](ChallengePlatformApi.md#ListChallengePlatforms) | **GET** /challengePlatforms                         | List challenge platforms |

# **GetChallengePlatform**

> ChallengePlatform GetChallengePlatform(challenge_platform_name)

Get a challenge platform

Returns the challenge platform specified

### Example

```R
library(openapi)

# Get a challenge platform
#
# prepare function argument(s)
var_challenge_platform_name <- "challenge_platform_name_example" # character | The unique identifier of the challenge platform.

api_instance <- ChallengePlatformApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetChallengePlatform(var_challenge_platform_namedata_file = "result.txt")
result <- api_instance$GetChallengePlatform(var_challenge_platform_name)
dput(result)
```

### Parameters

| Name                        | Type          | Description                                      | Notes |
| --------------------------- | ------------- | ------------------------------------------------ | ----- |
| **challenge_platform_name** | **character** | The unique identifier of the challenge platform. |

### Return type

[**ChallengePlatform**](ChallengePlatform.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

# **ListChallengePlatforms**

> ChallengePlatformsPage ListChallengePlatforms(challenge_platform_search_query = var.challenge_platform_search_query)

List challenge platforms

List challenge platforms

### Example

```R
library(openapi)

# List challenge platforms
#
# prepare function argument(s)
var_challenge_platform_search_query <- ChallengePlatformSearchQuery$new(123, 123, ChallengePlatformSort$new(), ChallengePlatformDirection$new(), "searchTerms_example") # ChallengePlatformSearchQuery | The search query used to find challenge platforms. (Optional)

api_instance <- ChallengePlatformApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ListChallengePlatforms(challenge_platform_search_query = var_challenge_platform_search_querydata_file = "result.txt")
result <- api_instance$ListChallengePlatforms(challenge_platform_search_query = var_challenge_platform_search_query)
dput(result)
```

### Parameters

| Name                                | Type                                    | Description                                        | Notes      |
| ----------------------------------- | --------------------------------------- | -------------------------------------------------- | ---------- |
| **challenge_platform_search_query** | [**ChallengePlatformSearchQuery**](.md) | The search query used to find challenge platforms. | [optional] |

### Return type

[**ChallengePlatformsPage**](ChallengePlatformsPage.md)

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
