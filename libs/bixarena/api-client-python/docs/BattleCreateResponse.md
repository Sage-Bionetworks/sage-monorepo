# BattleCreateResponse

A battle between two AI models.

## Properties

| Name           | Type                  | Description                             | Notes      |
| -------------- | --------------------- | --------------------------------------- | ---------- |
| **id**         | **str**               | Unique identifier (UUID) of the battle. |
| **title**      | **str**               | Title of the battle.                    | [optional] |
| **user_id**    | **str**               | UUID of a user.                         |
| **model1**     | [**Model**](Model.md) |                                         |
| **model2**     | [**Model**](Model.md) |                                         |
| **created_at** | **datetime**          | Timestamp when the entity was created.  |
| **ended_at**   | **datetime**          | Timestamp when the entity ended.        | [optional] |

## Example

```python
from bixarena_api_client.models.battle_create_response import BattleCreateResponse

# TODO update the JSON string below
json = "{}"
# create an instance of BattleCreateResponse from a JSON string
battle_create_response_instance = BattleCreateResponse.from_json(json)
# print the JSON string representation of the object
print(BattleCreateResponse.to_json())

# convert the object into a dict
battle_create_response_dict = battle_create_response_instance.to_dict()
# create an instance of BattleCreateResponse from a dict
battle_create_response_from_dict = BattleCreateResponse.from_dict(battle_create_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
