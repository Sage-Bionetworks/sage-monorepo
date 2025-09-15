# openchallenges_api_client_python.ChallengeApi

All URIs are relative to *https://openchallenges.io/api/v1*

| Method                                                             | HTTP request                              | Description                       |
| ------------------------------------------------------------------ | ----------------------------------------- | --------------------------------- |
| [**create_challenge**](ChallengeApi.md#create_challenge)           | **POST** /challenges                      | Create a challenge                |
| [**delete_challenge**](ChallengeApi.md#delete_challenge)           | **DELETE** /challenges/{challengeId}      | Delete a challenge                |
| [**get_challenge**](ChallengeApi.md#get_challenge)                 | **GET** /challenges/{challengeId}         | Get a challenge                   |
| [**get_challenge_json_ld**](ChallengeApi.md#get_challenge_json_ld) | **GET** /challenges/{challengeId}/json-ld | Get a challenge in JSON-LD format |
| [**list_challenges**](ChallengeApi.md#list_challenges)             | **GET** /challenges                       | List challenges                   |
| [**update_challenge**](ChallengeApi.md#update_challenge)           | **PUT** /challenges/{challengeId}         | Update an existing challenge      |

# **create_challenge**

> Challenge create_challenge(challenge_create_request)

Create a challenge

Create a challenge with the specified details

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.models.challenge_create_request import ChallengeCreateRequest
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
    challenge_create_request = openchallenges_api_client_python.ChallengeCreateRequest() # ChallengeCreateRequest |

    try:
        # Create a challenge
        api_response = api_instance.create_challenge(challenge_create_request)
        print("The response of ChallengeApi->create_challenge:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeApi->create_challenge: %s\n" % e)
```

### Parameters

| Name                         | Type                                                    | Description | Notes |
| ---------------------------- | ------------------------------------------------------- | ----------- | ----- |
| **challenge_create_request** | [**ChallengeCreateRequest**](ChallengeCreateRequest.md) |             |

### Return type

[**Challenge**](Challenge.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Challenge created successfully                                    | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_challenge**

> delete_challenge(challenge_id)

Delete a challenge

Deletes a challenge by its unique ID.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
    challenge_id = 56 # int | The unique identifier of the challenge.

    try:
        # Delete a challenge
        api_instance.delete_challenge(challenge_id)
    except Exception as e:
        print("Exception when calling ChallengeApi->delete_challenge: %s\n" % e)
```

### Parameters

| Name             | Type    | Description                             | Notes |
| ---------------- | ------- | --------------------------------------- | ----- |
| **challenge_id** | **int** | The unique identifier of the challenge. |

### Return type

void (empty response body)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **204**     | Challenge successfully deleted                                    | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_challenge**

> Challenge get_challenge(challenge_id)

Get a challenge

Returns the challenge specified

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
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

[jwtBearer](../README.md#jwtBearer)

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

# **get_challenge_json_ld**

> ChallengeJsonLd get_challenge_json_ld(challenge_id)

Get a challenge in JSON-LD format

Returns the challenge specified in JSON-LD format

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_json_ld import ChallengeJsonLd
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
    challenge_id = 56 # int | The unique identifier of the challenge.

    try:
        # Get a challenge in JSON-LD format
        api_response = api_instance.get_challenge_json_ld(challenge_id)
        print("The response of ChallengeApi->get_challenge_json_ld:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeApi->get_challenge_json_ld: %s\n" % e)
```

### Parameters

| Name             | Type    | Description                             | Notes |
| ---------------- | ------- | --------------------------------------- | ----- |
| **challenge_id** | **int** | The unique identifier of the challenge. |

### Return type

[**ChallengeJsonLd**](ChallengeJsonLd.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/ld+json, application/problem+json

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

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_search_query import ChallengeSearchQuery
from openchallenges_api_client_python.models.challenges_page import ChallengesPage
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
    challenge_search_query = openchallenges_api_client_python.ChallengeSearchQuery() # ChallengeSearchQuery | The search query used to find challenges. (optional)

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

[jwtBearer](../README.md#jwtBearer)

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

# **update_challenge**

> Challenge update_challenge(challenge_id, challenge_update_request)

Update an existing challenge

Updates an existing challenge.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.models.challenge_update_request import ChallengeUpdateRequest
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeApi(api_client)
    challenge_id = 56 # int | The unique identifier of the challenge.
    challenge_update_request = openchallenges_api_client_python.ChallengeUpdateRequest() # ChallengeUpdateRequest |

    try:
        # Update an existing challenge
        api_response = api_instance.update_challenge(challenge_id, challenge_update_request)
        print("The response of ChallengeApi->update_challenge:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeApi->update_challenge: %s\n" % e)
```

### Parameters

| Name                         | Type                                                    | Description                             | Notes |
| ---------------------------- | ------------------------------------------------------- | --------------------------------------- | ----- |
| **challenge_id**             | **int**                                                 | The unique identifier of the challenge. |
| **challenge_update_request** | [**ChallengeUpdateRequest**](ChallengeUpdateRequest.md) |                                         |

### Return type

[**Challenge**](Challenge.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Challenge successfully updated                                    | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
