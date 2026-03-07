# BattleUpdateRequest

The information used to update a battle.

## Properties

| Name         | Type         | Description                      | Notes      |
| ------------ | ------------ | -------------------------------- | ---------- |
| **title**    | **str**      | Title of the battle.             | [optional] |
| **ended_at** | **datetime** | Timestamp when the entity ended. | [optional] |

## Example

```python
from bixarena_api_client.models.battle_update_request import BattleUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleUpdateRequest from a JSON string
battle_update_request_instance = BattleUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleUpdateRequest.to_json())

# convert the object into a dict
battle_update_request_dict = battle_update_request_instance.to_dict()
# create an instance of BattleUpdateRequest from a dict
battle_update_request_from_dict = BattleUpdateRequest.from_dict(battle_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
