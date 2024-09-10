# ChallengeInputDataTypeApi

All URIs are relative to _http://localhost/v1_

| Method                                                                                      | HTTP request                     | Description                     |
| ------------------------------------------------------------------------------------------- | -------------------------------- | ------------------------------- |
| [**ListChallengeInputDataTypes**](ChallengeInputDataTypeApi.md#ListChallengeInputDataTypes) | **GET** /challengeInputDataTypes | List challenge input data types |

# **ListChallengeInputDataTypes**

> ChallengeInputDataTypesPage ListChallengeInputDataTypes(challenge_input_data_type_search_query = var.challenge_input_data_type_search_query)

List challenge input data types

List challenge input data types

### Example

```R
library(openapi)

# List challenge input data types
#
# prepare function argument(s)
var_challenge_input_data_type_search_query <- ChallengeInputDataTypeSearchQuery$new(123, 123, ChallengeInputDataTypeSort$new(), ChallengeInputDataTypeDirection$new(), "searchTerms_example") # ChallengeInputDataTypeSearchQuery | The search query used to find challenge input data types. (Optional)

api_instance <- ChallengeInputDataTypeApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ListChallengeInputDataTypes(challenge_input_data_type_search_query = var_challenge_input_data_type_search_querydata_file = "result.txt")
result <- api_instance$ListChallengeInputDataTypes(challenge_input_data_type_search_query = var_challenge_input_data_type_search_query)
dput(result)
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
