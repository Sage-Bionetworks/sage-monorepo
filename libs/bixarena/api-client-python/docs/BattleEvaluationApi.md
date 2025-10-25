# bixarena_api_client.BattleEvaluationApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                                          | HTTP request                             | Description                |
| ------------------------------------------------------------------------------- | ---------------------------------------- | -------------------------- |
| [**create_battle_evaluation**](BattleEvaluationApi.md#create_battle_evaluation) | **POST** /battles/{battleId}/evaluations | Create a battle evaluation |

# **create_battle_evaluation**

> BattleEvaluation create_battle_evaluation(battle_id, battle_evaluation_create_request)

Create a battle evaluation

Create a new battle evaluation for a battle.

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
    api_instance = bixarena_api_client.BattleEvaluationApi(api_client)
    battle_id = 'battle_id_example' # str | The unique identifier of the battle
    battle_evaluation_create_request = bixarena_api_client.BattleEvaluationCreateRequest() # BattleEvaluationCreateRequest |

    try:
        # Create a battle evaluation
        api_response = api_instance.create_battle_evaluation(battle_id, battle_evaluation_create_request)
        print("The response of BattleEvaluationApi->create_battle_evaluation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BattleEvaluationApi->create_battle_evaluation: %s\n" % e)
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

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | BattleEvaluation created successfully                             | -                |
| **400**     | Invalid request                                                   | -                |
| **401**     | Unauthorized                                                      | -                |
| **403**     | The user does not have the permission to perform this action      | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
