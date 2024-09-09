# ChallengeApi

All URIs are relative to _http://localhost/v1_

| Method                                               | HTTP request                      | Description     |
| ---------------------------------------------------- | --------------------------------- | --------------- |
| [**GetChallenge**](ChallengeApi.md#GetChallenge)     | **GET** /challenges/{challengeId} | Get a challenge |
| [**ListChallenges**](ChallengeApi.md#ListChallenges) | **GET** /challenges               | List challenges |

# **GetChallenge**

> Challenge GetChallenge(challenge_id)

Get a challenge

Returns the challenge specified

### Example

```R
library(openapi)

# Get a challenge
#
# prepare function argument(s)
var_challenge_id <- 56 # integer | The unique identifier of the challenge.

api_instance <- ChallengeApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetChallenge(var_challenge_iddata_file = "result.txt")
result <- api_instance$GetChallenge(var_challenge_id)
dput(result)
```

### Parameters

| Name             | Type        | Description                             | Notes |
| ---------------- | ----------- | --------------------------------------- | ----- |
| **challenge_id** | **integer** | The unique identifier of the challenge. |

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

# **ListChallenges**

> ChallengesPage ListChallenges(challenge_search_query = var.challenge_search_query)

List challenges

List challenges

### Example

```R
library(openapi)

# List challenges
#
# prepare function argument(s)
var_challenge_search_query <- ChallengeSearchQuery$new(123, 123, ChallengeSort$new(), ChallengeDirection$new(), c(ChallengeDifficulty$new()), c(ChallengeIncentive$new()), "minStartDate_example", "maxStartDate_example", c("platforms_example"), c(123), c("inputDataTypes_example"), c(ChallengeStatus$new()), c(ChallengeSubmissionType$new()), "searchTerms_example") # ChallengeSearchQuery | The search query used to find challenges. (Optional)

api_instance <- ChallengeApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ListChallenges(challenge_search_query = var_challenge_search_querydata_file = "result.txt")
result <- api_instance$ListChallenges(challenge_search_query = var_challenge_search_query)
dput(result)
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
