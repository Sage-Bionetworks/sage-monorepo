# bixarena_api_client.BattleApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                          | HTTP request                   | Description        |
| ----------------------------------------------- | ------------------------------ | ------------------ |
| [**create_battle**](BattleApi.md#create_battle) | **POST** /battles              | Create a battle    |
| [**delete_battle**](BattleApi.md#delete_battle) | **DELETE** /battles/{battleId} | Delete a battle    |
| [**get_battle**](BattleApi.md#get_battle)       | **GET** /battles/{battleId}    | Get a battle by ID |
| [**list_battles**](BattleApi.md#list_battles)   | **GET** /battles               | List battles       |
| [**update_battle**](BattleApi.md#update_battle) | **PATCH** /battles/{battleId}  | Update a battle    |

# **create_battle**

> Battle create_battle(battle_create_request)

Create a battle

Create a new battle between two AI models.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle import Battle
from bixarena_api_client.models.battle_create_request import BattleCreateRequest
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
    api_instance = bixarena_api_client.BattleApi(api_client)
    battle_create_request = bixarena_api_client.BattleCreateRequest() # BattleCreateRequest |

    try:
        # Create a battle
        api_response = api_instance.create_battle(battle_create_request)
        print("The response of BattleApi->create_battle:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle: %s\n" % e)
```

### Parameters

| Name                      | Type                                              | Description | Notes |
| ------------------------- | ------------------------------------------------- | ----------- | ----- |
| **battle_create_request** | [**BattleCreateRequest**](BattleCreateRequest.md) |             |

### Return type

[**Battle**](Battle.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Battle created successfully                                       | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_battle**

> delete_battle(battle_id)

Delete a battle

Delete a battle by its unique identifier

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
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
    api_instance = bixarena_api_client.BattleApi(api_client)
    battle_id = 'battle_id_example' # str | The unique identifier of the battle

    try:
        # Delete a battle
        api_instance.delete_battle(battle_id)
    except Exception as e:
        print("Exception when calling BattleApi->delete_battle: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

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
| **204**     | Battle deleted successfully                                       | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_battle**

> Battle get_battle(battle_id)

Get a battle by ID

Returns a single battle by its unique identifier

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle import Battle
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
    api_instance = bixarena_api_client.BattleApi(api_client)
    battle_id = 'battle_id_example' # str | The unique identifier of the battle

    try:
        # Get a battle by ID
        api_response = api_instance.get_battle(battle_id)
        print("The response of BattleApi->get_battle:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->get_battle: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

### Return type

[**Battle**](Battle.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **401**     | Unauthorized                                                      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_battles**

> BattlePage list_battles(battle_search_query=battle_search_query)

List battles

List battles with optional filtering and pagination

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_page import BattlePage
from bixarena_api_client.models.battle_search_query import BattleSearchQuery
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
    api_instance = bixarena_api_client.BattleApi(api_client)
    battle_search_query = bixarena_api_client.BattleSearchQuery() # BattleSearchQuery | The search query used to find and filter battles. (optional)

    try:
        # List battles
        api_response = api_instance.list_battles(battle_search_query=battle_search_query)
        print("The response of BattleApi->list_battles:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->list_battles: %s\n" % e)
```

### Parameters

| Name                    | Type                         | Description                                       | Notes      |
| ----------------------- | ---------------------------- | ------------------------------------------------- | ---------- |
| **battle_search_query** | [**BattleSearchQuery**](.md) | The search query used to find and filter battles. | [optional] |

### Return type

[**BattlePage**](BattlePage.md)

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
| **401**     | Unauthorized                                                      | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_battle**

> Battle update_battle(battle_id, battle_update_request)

Update a battle

Update a battle's end time or other properties

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle import Battle
from bixarena_api_client.models.battle_update_request import BattleUpdateRequest
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
    api_instance = bixarena_api_client.BattleApi(api_client)
    battle_id = 'battle_id_example' # str | The unique identifier of the battle
    battle_update_request = bixarena_api_client.BattleUpdateRequest() # BattleUpdateRequest |

    try:
        # Update a battle
        api_response = api_instance.update_battle(battle_id, battle_update_request)
        print("The response of BattleApi->update_battle:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->update_battle: %s\n" % e)
```

### Parameters

| Name                      | Type                                              | Description                         | Notes |
| ------------------------- | ------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**             | **str**                                           | The unique identifier of the battle |
| **battle_update_request** | [**BattleUpdateRequest**](BattleUpdateRequest.md) |                                     |

### Return type

[**Battle**](Battle.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Battle updated successfully                                       | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
