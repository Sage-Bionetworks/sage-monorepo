# bixarena_api_client.BattleApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                                                      | HTTP request                                            | Description                                 |
| ------------------------------------------------------------------------------------------- | ------------------------------------------------------- | ------------------------------------------- |
| [**create_battle**](BattleApi.md#create_battle)                                             | **POST** /battles                                       | Create a battle                             |
| [**create_battle_categorization**](BattleApi.md#create_battle_categorization)               | **POST** /battles/{battleId}/categorizations            | Create a battle categorization              |
| [**create_battle_evaluation**](BattleApi.md#create_battle_evaluation)                       | **POST** /battles/{battleId}/evaluations                | Create a battle evaluation                  |
| [**create_battle_round**](BattleApi.md#create_battle_round)                                 | **POST** /battles/{battleId}/rounds                     | Create a battle round                       |
| [**create_battle_round_completion**](BattleApi.md#create_battle_round_completion)           | **POST** /battles/{battleId}/rounds/{roundId}/stream    | Stream a chat completion for a battle round |
| [**create_battle_validation**](BattleApi.md#create_battle_validation)                       | **POST** /battles/{battleId}/validations                | Create a battle validation                  |
| [**delete_battle**](BattleApi.md#delete_battle)                                             | **DELETE** /battles/{battleId}                          | Delete a battle                             |
| [**get_battle**](BattleApi.md#get_battle)                                                   | **GET** /battles/{battleId}                             | Get a battle by ID                          |
| [**list_battle_categorizations**](BattleApi.md#list_battle_categorizations)                 | **GET** /battles/{battleId}/categorizations             | List battle categorizations                 |
| [**list_battle_validations**](BattleApi.md#list_battle_validations)                         | **GET** /battles/{battleId}/validations                 | List battle validations                     |
| [**list_battles**](BattleApi.md#list_battles)                                               | **GET** /battles                                        | List battles                                |
| [**run_battle_categorization**](BattleApi.md#run_battle_categorization)                     | **POST** /battles/{battleId}/categorizations/run        | Run an automated categorization             |
| [**run_battle_validation**](BattleApi.md#run_battle_validation)                             | **POST** /battles/{battleId}/validations/run            | Run an automated validation method          |
| [**set_effective_battle_categorization**](BattleApi.md#set_effective_battle_categorization) | **PATCH** /battles/{battleId}/categorizations/effective | Set effective battle categorization         |
| [**set_effective_validation**](BattleApi.md#set_effective_validation)                       | **PATCH** /battles/{battleId}/validations/effective     | Set effective validation                    |
| [**update_battle**](BattleApi.md#update_battle)                                             | **PATCH** /battles/{battleId}                           | Update a battle                             |
| [**update_battle_round**](BattleApi.md#update_battle_round)                                 | **PATCH** /battles/{battleId}/rounds/{roundId}          | Update a battle round                       |

# **create_battle**

> BattleCreateResponse create_battle(battle_create_request)

Create a battle

Create a new battle between two AI models.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_create_request import BattleCreateRequest
from bixarena_api_client.models.battle_create_response import BattleCreateResponse
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

[**BattleCreateResponse**](BattleCreateResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | Battle created successfully                                                                       | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **409**     | The request conflicts with current state of the target resource                                   | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_battle_categorization**

> BattleCategorizationResponse create_battle_categorization(battle_id, battle_categorization_create_request)

Create a battle categorization

Manually categorize a battle. The created categorization is automatically set as the effective categorization.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_categorization_create_request import BattleCategorizationCreateRequest
from bixarena_api_client.models.battle_categorization_response import BattleCategorizationResponse
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
    battle_categorization_create_request = bixarena_api_client.BattleCategorizationCreateRequest() # BattleCategorizationCreateRequest |

    try:
        # Create a battle categorization
        api_response = api_instance.create_battle_categorization(battle_id, battle_categorization_create_request)
        print("The response of BattleApi->create_battle_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle_categorization: %s\n" % e)
```

### Parameters

| Name                                     | Type                                                                          | Description                         | Notes |
| ---------------------------------------- | ----------------------------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                            | **str**                                                                       | The unique identifier of the battle |
| **battle_categorization_create_request** | [**BattleCategorizationCreateRequest**](BattleCategorizationCreateRequest.md) |                                     |

### Return type

[**BattleCategorizationResponse**](BattleCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Battle categorization created successfully                        | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_battle_evaluation**

> BattleEvaluation create_battle_evaluation(battle_id, battle_evaluation_create_request)

Create a battle evaluation

Record the outcome of a battle.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_evaluation import BattleEvaluation
from bixarena_api_client.models.battle_evaluation_create_request import BattleEvaluationCreateRequest
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
    battle_evaluation_create_request = bixarena_api_client.BattleEvaluationCreateRequest() # BattleEvaluationCreateRequest |

    try:
        # Create a battle evaluation
        api_response = api_instance.create_battle_evaluation(battle_id, battle_evaluation_create_request)
        print("The response of BattleApi->create_battle_evaluation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle_evaluation: %s\n" % e)
```

### Parameters

| Name                                 | Type                                                                  | Description                         | Notes |
| ------------------------------------ | --------------------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                        | **str**                                                               | The unique identifier of the battle |
| **battle_evaluation_create_request** | [**BattleEvaluationCreateRequest**](BattleEvaluationCreateRequest.md) |                                     |

### Return type

[**BattleEvaluation**](BattleEvaluation.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | BattleEvaluation created successfully                                                             | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_battle_round**

> BattleRound create_battle_round(battle_id, battle_round_create_request)

Create a battle round

Create a new round for a given battle.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_round import BattleRound
from bixarena_api_client.models.battle_round_create_request import BattleRoundCreateRequest
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
    battle_round_create_request = bixarena_api_client.BattleRoundCreateRequest() # BattleRoundCreateRequest |

    try:
        # Create a battle round
        api_response = api_instance.create_battle_round(battle_id, battle_round_create_request)
        print("The response of BattleApi->create_battle_round:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle_round: %s\n" % e)
```

### Parameters

| Name                            | Type                                                        | Description                         | Notes |
| ------------------------------- | ----------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                   | **str**                                                     | The unique identifier of the battle |
| **battle_round_create_request** | [**BattleRoundCreateRequest**](BattleRoundCreateRequest.md) |                                     |

### Return type

[**BattleRound**](BattleRound.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | Battle round created successfully                                                                 | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_battle_round_completion**

> ModelChatCompletionChunk create_battle_round_completion(battle_id, round_id, model_id)

Stream a chat completion for a battle round

Sends the round's prompt and conversation history to the specified model via ai-service and returns the response as a streaming HTTP response (text/event-stream). The completed response is persisted as a message after the stream ends.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.model_chat_completion_chunk import ModelChatCompletionChunk
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
    round_id = 'round_id_example' # str | The unique identifier of the battle round
    model_id = 'model_id_example' # str | The model to stream

    try:
        # Stream a chat completion for a battle round
        api_response = api_instance.create_battle_round_completion(battle_id, round_id, model_id)
        print("The response of BattleApi->create_battle_round_completion:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle_round_completion: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                               | Notes |
| ------------- | ------- | ----------------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle       |
| **round_id**  | **str** | The unique identifier of the battle round |
| **model_id**  | **str** | The model to stream                       |

### Return type

[**ModelChatCompletionChunk**](ModelChatCompletionChunk.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/event-stream, application/problem+json, application/json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Streaming HTTP response with chat completion chunks                                               | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_battle_validation**

> BattleValidationResponse create_battle_validation(battle_id, battle_validation_create_request)

Create a battle validation

Manually validate or invalidate a battle (admin only). The created validation is automatically set as the effective validation for the battle.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_validation_create_request import BattleValidationCreateRequest
from bixarena_api_client.models.battle_validation_response import BattleValidationResponse
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
    battle_validation_create_request = bixarena_api_client.BattleValidationCreateRequest() # BattleValidationCreateRequest |

    try:
        # Create a battle validation
        api_response = api_instance.create_battle_validation(battle_id, battle_validation_create_request)
        print("The response of BattleApi->create_battle_validation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->create_battle_validation: %s\n" % e)
```

### Parameters

| Name                                 | Type                                                                  | Description                         | Notes |
| ------------------------------------ | --------------------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                        | **str**                                                               | The unique identifier of the battle |
| **battle_validation_create_request** | [**BattleValidationCreateRequest**](BattleValidationCreateRequest.md) |                                     |

### Return type

[**BattleValidationResponse**](BattleValidationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Battle validation created successfully                            | -                |
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
- **Accept**: application/problem+json, application/json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **204**     | Battle deleted successfully                                                                       | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

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

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_battle_categorizations**

> List[BattleCategorizationResponse] list_battle_categorizations(battle_id)

List battle categorizations

Get all categorizations for a battle.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_categorization_response import BattleCategorizationResponse
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
        # List battle categorizations
        api_response = api_instance.list_battle_categorizations(battle_id)
        print("The response of BattleApi->list_battle_categorizations:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->list_battle_categorizations: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

### Return type

[**List[BattleCategorizationResponse]**](BattleCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | List of battle categorizations                                    | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_battle_validations**

> List[BattleValidationResponse] list_battle_validations(battle_id)

List battle validations

Get all validations for a battle (admin only).

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_validation_response import BattleValidationResponse
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
        # List battle validations
        api_response = api_instance.list_battle_validations(battle_id)
        print("The response of BattleApi->list_battle_validations:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->list_battle_validations: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

### Return type

[**List[BattleValidationResponse]**](BattleValidationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | List of battle validations                                        | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
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

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **run_battle_categorization**

> BattleCategorizationResponse run_battle_categorization(battle_id)

Run an automated categorization

Run an automated AI categorization against a battle. Always returns 201 with the persisted row — when the AI could not match any category, the row is still persisted with an empty `categories` array to preserve the audit trail and avoid redundant re-classification. Returns 409 if the battle is not biomedical.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_categorization_response import BattleCategorizationResponse
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
        # Run an automated categorization
        api_response = api_instance.run_battle_categorization(battle_id)
        print("The response of BattleApi->run_battle_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->run_battle_categorization: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

### Return type

[**BattleCategorizationResponse**](BattleCategorizationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Categorization completed and persisted successfully               | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **run_battle_validation**

> BattleValidationResponse run_battle_validation(battle_id)

Run an automated validation method

Run an automated validation method against a battle and return the result. Useful for backfilling validations on battles created before automated validation was implemented. Admin only.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_validation_response import BattleValidationResponse
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
        # Run an automated validation method
        api_response = api_instance.run_battle_validation(battle_id)
        print("The response of BattleApi->run_battle_validation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->run_battle_validation: %s\n" % e)
```

### Parameters

| Name          | Type    | Description                         | Notes |
| ------------- | ------- | ----------------------------------- | ----- |
| **battle_id** | **str** | The unique identifier of the battle |

### Return type

[**BattleValidationResponse**](BattleValidationResponse.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Validation completed and persisted successfully                   | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_effective_battle_categorization**

> Battle set_effective_battle_categorization(battle_id, set_effective_categorization_request)

Set effective battle categorization

Set or clear the effective categorization for a battle by pointing at a row from history. Pass null to clear. Returns 409 if the battle is not biomedical — the gate applies to both setting and clearing; non-biomedical battles are not eligible for any effective categorization state change.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle import Battle
from bixarena_api_client.models.set_effective_categorization_request import SetEffectiveCategorizationRequest
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
    set_effective_categorization_request = bixarena_api_client.SetEffectiveCategorizationRequest() # SetEffectiveCategorizationRequest |

    try:
        # Set effective battle categorization
        api_response = api_instance.set_effective_battle_categorization(battle_id, set_effective_categorization_request)
        print("The response of BattleApi->set_effective_battle_categorization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->set_effective_battle_categorization: %s\n" % e)
```

### Parameters

| Name                                     | Type                                                                          | Description                         | Notes |
| ---------------------------------------- | ----------------------------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                            | **str**                                                                       | The unique identifier of the battle |
| **set_effective_categorization_request** | [**SetEffectiveCategorizationRequest**](SetEffectiveCategorizationRequest.md) |                                     |

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
| **200**     | Effective categorization updated successfully                     | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_effective_validation**

> Battle set_effective_validation(battle_id, set_effective_validation_request)

Set effective validation

Set or clear the effective validation for a battle. The effective validation determines whether a battle counts in stats (only battles with a positive effective validation are included). Admin only.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle import Battle
from bixarena_api_client.models.set_effective_validation_request import SetEffectiveValidationRequest
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
    set_effective_validation_request = bixarena_api_client.SetEffectiveValidationRequest() # SetEffectiveValidationRequest |

    try:
        # Set effective validation
        api_response = api_instance.set_effective_validation(battle_id, set_effective_validation_request)
        print("The response of BattleApi->set_effective_validation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->set_effective_validation: %s\n" % e)
```

### Parameters

| Name                                 | Type                                                                  | Description                         | Notes |
| ------------------------------------ | --------------------------------------------------------------------- | ----------------------------------- | ----- |
| **battle_id**                        | **str**                                                               | The unique identifier of the battle |
| **set_effective_validation_request** | [**SetEffectiveValidationRequest**](SetEffectiveValidationRequest.md) |                                     |

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
| **200**     | Effective validation updated successfully                         | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
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

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Battle updated successfully                                                                       | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_battle_round**

> BattleRound update_battle_round(battle_id, round_id, battle_round_update_request)

Update a battle round

Update an existing battle round.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.battle_round import BattleRound
from bixarena_api_client.models.battle_round_update_request import BattleRoundUpdateRequest
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
    round_id = 'round_id_example' # str | The unique identifier of the battle round
    battle_round_update_request = bixarena_api_client.BattleRoundUpdateRequest() # BattleRoundUpdateRequest |

    try:
        # Update a battle round
        api_response = api_instance.update_battle_round(battle_id, round_id, battle_round_update_request)
        print("The response of BattleApi->update_battle_round:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleApi->update_battle_round: %s\n" % e)
```

### Parameters

| Name                            | Type                                                        | Description                               | Notes |
| ------------------------------- | ----------------------------------------------------------- | ----------------------------------------- | ----- |
| **battle_id**                   | **str**                                                     | The unique identifier of the battle       |
| **round_id**                    | **str**                                                     | The unique identifier of the battle round |
| **battle_round_update_request** | [**BattleRoundUpdateRequest**](BattleRoundUpdateRequest.md) |                                           |

### Return type

[**BattleRound**](BattleRound.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Battle round updated successfully                                                                 | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
