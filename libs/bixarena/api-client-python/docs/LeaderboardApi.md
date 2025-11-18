# bixarena_api_client.LeaderboardApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                                       | HTTP request                                            | Description                      |
| ---------------------------------------------------------------------------- | ------------------------------------------------------- | -------------------------------- |
| [**get_leaderboard**](LeaderboardApi.md#get_leaderboard)                     | **GET** /leaderboards/{leaderboardId}                   | Get leaderboard entries          |
| [**get_leaderboard_snapshots**](LeaderboardApi.md#get_leaderboard_snapshots) | **GET** /leaderboards/{leaderboardId}/snapshots         | Get public leaderboard snapshots |
| [**get_model_history**](LeaderboardApi.md#get_model_history)                 | **GET** /leaderboards/{leaderboardId}/history/{modelId} | Get model performance history    |
| [**list_leaderboards**](LeaderboardApi.md#list_leaderboards)                 | **GET** /leaderboards                                   | List all available leaderboards  |

# **get_leaderboard**

> LeaderboardEntryPage get_leaderboard(leaderboard_id, leaderboard_search_query=leaderboard_search_query)

Get leaderboard entries

Get paginated leaderboard entries for a specific leaderboard

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.leaderboard_entry_page import LeaderboardEntryPage
from bixarena_api_client.models.leaderboard_search_query import LeaderboardSearchQuery
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.LeaderboardApi(api_client)
    leaderboard_id = 'open-source' # str | The unique identifier of a leaderboard
    leaderboard_search_query = bixarena_api_client.LeaderboardSearchQuery() # LeaderboardSearchQuery | The search query used to find and filter leaderboard entries. (optional)

    try:
        # Get leaderboard entries
        api_response = api_instance.get_leaderboard(leaderboard_id, leaderboard_search_query=leaderboard_search_query)
        print("The response of LeaderboardApi->get_leaderboard:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LeaderboardApi->get_leaderboard: %s\n" % e)
```

### Parameters

| Name                         | Type                              | Description                                                   | Notes      |
| ---------------------------- | --------------------------------- | ------------------------------------------------------------- | ---------- |
| **leaderboard_id**           | **str**                           | The unique identifier of a leaderboard                        |
| **leaderboard_search_query** | [**LeaderboardSearchQuery**](.md) | The search query used to find and filter leaderboard entries. | [optional] |

### Return type

[**LeaderboardEntryPage**](LeaderboardEntryPage.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_leaderboard_snapshots**

> LeaderboardSnapshotPage get_leaderboard_snapshots(leaderboard_id, leaderboard_snapshot_query=leaderboard_snapshot_query)

Get public leaderboard snapshots

Get a paginated list of public snapshots for a leaderboard.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.leaderboard_snapshot_page import LeaderboardSnapshotPage
from bixarena_api_client.models.leaderboard_snapshot_query import LeaderboardSnapshotQuery
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.LeaderboardApi(api_client)
    leaderboard_id = 'open-source' # str | The unique identifier of a leaderboard
    leaderboard_snapshot_query = bixarena_api_client.LeaderboardSnapshotQuery() # LeaderboardSnapshotQuery | The query used to filter and paginate leaderboard snapshots. (optional)

    try:
        # Get public leaderboard snapshots
        api_response = api_instance.get_leaderboard_snapshots(leaderboard_id, leaderboard_snapshot_query=leaderboard_snapshot_query)
        print("The response of LeaderboardApi->get_leaderboard_snapshots:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LeaderboardApi->get_leaderboard_snapshots: %s\n" % e)
```

### Parameters

| Name                           | Type                                | Description                                                  | Notes      |
| ------------------------------ | ----------------------------------- | ------------------------------------------------------------ | ---------- |
| **leaderboard_id**             | **str**                             | The unique identifier of a leaderboard                       |
| **leaderboard_snapshot_query** | [**LeaderboardSnapshotQuery**](.md) | The query used to filter and paginate leaderboard snapshots. | [optional] |

### Return type

[**LeaderboardSnapshotPage**](LeaderboardSnapshotPage.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_model_history**

> LeaderboardModelHistoryPage get_model_history(leaderboard_id, model_id, leaderboard_model_history_query=leaderboard_model_history_query)

Get model performance history

Get historical performance data for a specific model in a leaderboard

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.leaderboard_model_history_page import LeaderboardModelHistoryPage
from bixarena_api_client.models.leaderboard_model_history_query import LeaderboardModelHistoryQuery
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.LeaderboardApi(api_client)
    leaderboard_id = 'open-source' # str | The unique identifier of a leaderboard
    model_id = 'model_456' # str | The unique identifier of a model
    leaderboard_model_history_query = bixarena_api_client.LeaderboardModelHistoryQuery() # LeaderboardModelHistoryQuery | The query used to filter and paginate historical model performance data. (optional)

    try:
        # Get model performance history
        api_response = api_instance.get_model_history(leaderboard_id, model_id, leaderboard_model_history_query=leaderboard_model_history_query)
        print("The response of LeaderboardApi->get_model_history:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LeaderboardApi->get_model_history: %s\n" % e)
```

### Parameters

| Name                                | Type                                    | Description                                                              | Notes      |
| ----------------------------------- | --------------------------------------- | ------------------------------------------------------------------------ | ---------- |
| **leaderboard_id**                  | **str**                                 | The unique identifier of a leaderboard                                   |
| **model_id**                        | **str**                                 | The unique identifier of a model                                         |
| **leaderboard_model_history_query** | [**LeaderboardModelHistoryQuery**](.md) | The query used to filter and paginate historical model performance data. | [optional] |

### Return type

[**LeaderboardModelHistoryPage**](LeaderboardModelHistoryPage.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_leaderboards**

> List[LeaderboardListInner] list_leaderboards()

List all available leaderboards

Get a list of all available leaderboards with their metadata

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.leaderboard_list_inner import LeaderboardListInner
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.LeaderboardApi(api_client)

    try:
        # List all available leaderboards
        api_response = api_instance.list_leaderboards()
        print("The response of LeaderboardApi->list_leaderboards:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LeaderboardApi->list_leaderboards: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List[LeaderboardListInner]**](LeaderboardListInner.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
