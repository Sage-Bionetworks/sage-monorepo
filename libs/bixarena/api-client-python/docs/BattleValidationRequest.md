# BattleValidationRequest

The prompts from a battle to validate for biomedical relevance.

## Properties

| Name        | Type          | Description                                        | Notes |
| ----------- | ------------- | -------------------------------------------------- | ----- |
| **prompts** | **List[str]** | List of user prompts from all rounds of the battle |

## Example

```python
from bixarena_api_client.models.battle_validation_request import BattleValidationRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleValidationRequest from a JSON string
battle_validation_request_instance = BattleValidationRequest.from_json(json)
# print the JSON string representation of the object
print(BattleValidationRequest.to_json())

# convert the object into a dict
battle_validation_request_dict = battle_validation_request_instance.to_dict()
# create an instance of BattleValidationRequest from a dict
battle_validation_request_from_dict = BattleValidationRequest.from_dict(battle_validation_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
