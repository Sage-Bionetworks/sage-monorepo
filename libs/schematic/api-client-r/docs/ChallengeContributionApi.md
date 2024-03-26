# ChallengeContributionApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ListChallengeContributions**](ChallengeContributionApi.md#ListChallengeContributions) | **GET** /challenges/{challengeId}/contributions | List challenge contributions


# **ListChallengeContributions**
> ChallengeContributionsPage ListChallengeContributions(challenge_id)

List challenge contributions

List challenge contributions

### Example
```R
library(openapi)

# List challenge contributions
#
# prepare function argument(s)
var_challenge_id <- 56 # integer | The unique identifier of the challenge.

api_instance <- ChallengeContributionApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ListChallengeContributions(var_challenge_iddata_file = "result.txt")
result <- api_instance$ListChallengeContributions(var_challenge_id)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **challenge_id** | **integer**| The unique identifier of the challenge. | 

### Return type

[**ChallengeContributionsPage**](ChallengeContributionsPage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

