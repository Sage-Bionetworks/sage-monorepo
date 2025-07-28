# openchallenges_api_client_python.ChallengeParticipationApi

All URIs are relative to _http://localhost/v1_

| Method                                                                                            | HTTP request                                                              | Description                               |
| ------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- | ----------------------------------------- |
| [**create_challenge_participation**](ChallengeParticipationApi.md#create_challenge_participation) | **POST** /organizations/{org}/participations                              | Create a new challenge participation      |
| [**delete_challenge_participation**](ChallengeParticipationApi.md#delete_challenge_participation) | **DELETE** /organizations/{org}/participations/{challengeId}/roles/{role} | Delete a specific challenge participation |

# **create_challenge_participation**

> ChallengeParticipation create_challenge_participation(org, challenge_participation_create_request)

Create a new challenge participation

Creates a new challenge participation.

### Example

- Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_participation import ChallengeParticipation
from openchallenges_api_client_python.models.challenge_participation_create_request import ChallengeParticipationCreateRequest
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeParticipationApi(api_client)
    org = 'dream' # str | The id or login of the organization.
    challenge_participation_create_request = openchallenges_api_client_python.ChallengeParticipationCreateRequest() # ChallengeParticipationCreateRequest |

    try:
        # Create a new challenge participation
        api_response = api_instance.create_challenge_participation(org, challenge_participation_create_request)
        print("The response of ChallengeParticipationApi->create_challenge_participation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChallengeParticipationApi->create_challenge_participation: %s\n" % e)
```

### Parameters

| Name                                       | Type                                                                              | Description                          | Notes |
| ------------------------------------------ | --------------------------------------------------------------------------------- | ------------------------------------ | ----- |
| **org**                                    | **str**                                                                           | The id or login of the organization. |
| **challenge_participation_create_request** | [**ChallengeParticipationCreateRequest**](ChallengeParticipationCreateRequest.md) |                                      |

### Return type

[**ChallengeParticipation**](ChallengeParticipation.md)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Participation created successfully                                | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_challenge_participation**

> delete_challenge_participation(org, challenge_id, role)

Delete a specific challenge participation

Delete a specific challenge participation.

### Example

- Bearer (api_key) Authentication (apiBearerAuth):

```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge_participation_role import ChallengeParticipationRole
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.ChallengeParticipationApi(api_client)
    org = 'dream' # str | The id or login of the organization.
    challenge_id = 56 # int | The unique identifier of the challenge.
    role = openchallenges_api_client_python.ChallengeParticipationRole() # ChallengeParticipationRole | A challenge participation role.

    try:
        # Delete a specific challenge participation
        api_instance.delete_challenge_participation(org, challenge_id, role)
    except Exception as e:
        print("Exception when calling ChallengeParticipationApi->delete_challenge_participation: %s\n" % e)
```

### Parameters

| Name             | Type                                  | Description                             | Notes |
| ---------------- | ------------------------------------- | --------------------------------------- | ----- |
| **org**          | **str**                               | The id or login of the organization.    |
| **challenge_id** | **int**                               | The unique identifier of the challenge. |
| **role**         | [**ChallengeParticipationRole**](.md) | A challenge participation role.         |

### Return type

void (empty response body)

### Authorization

[apiBearerAuth](../README.md#apiBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **204**     | Participation deleted successfully                                | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
