# BattleValidationRunRequest

Request to run an automated validation method against a battle.

## Properties

| Name       | Type    | Description                                                                                                             | Notes      |
| ---------- | ------- | ----------------------------------------------------------------------------------------------------------------------- | ---------- |
| **method** | **str** | Validation method to run (e.g. &#39;openrouter-haiku-v1&#39;). If not specified, the default configured method is used. | [optional] |

## Example

```python
from bixarena_api_client.models.battle_validation_run_request import BattleValidationRunRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleValidationRunRequest from a JSON string
battle_validation_run_request_instance = BattleValidationRunRequest.from_json(json)
# print the JSON string representation of the object
print(BattleValidationRunRequest.to_json())

# convert the object into a dict
battle_validation_run_request_dict = battle_validation_run_request_instance.to_dict()
# create an instance of BattleValidationRunRequest from a dict
battle_validation_run_request_from_dict = BattleValidationRunRequest.from_dict(battle_validation_run_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
