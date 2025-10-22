# BattleCreateRequest

The information used to create a new battle.

## Properties

| Name               | Type    | Description          | Notes      |
| ------------------ | ------- | -------------------- | ---------- |
| **title**          | **str** | Title of the battle. | [optional] |
| **left_model_id**  | **str** | UUID of an AI model. |
| **right_model_id** | **str** | UUID of an AI model. |

## Example

```python
from bixarena_api_client.models.battle_create_request import BattleCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleCreateRequest from a JSON string
battle_create_request_instance = BattleCreateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleCreateRequest.to_json())

# convert the object into a dict
battle_create_request_dict = battle_create_request_instance.to_dict()
# create an instance of BattleCreateRequest from a dict
battle_create_request_from_dict = BattleCreateRequest.from_dict(battle_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
