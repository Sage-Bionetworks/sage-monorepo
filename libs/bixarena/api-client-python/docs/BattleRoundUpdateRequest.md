# BattleRoundUpdateRequest

Payload to update model responses for an existing battle round.

## Properties

| Name               | Type                                  | Description | Notes |
| ------------------ | ------------------------------------- | ----------- | ----- |
| **model1_message** | [**MessageCreate**](MessageCreate.md) |             |
| **model2_message** | [**MessageCreate**](MessageCreate.md) |             |

## Example

```python
from bixarena_api_client.models.battle_round_update_request import BattleRoundUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleRoundUpdateRequest from a JSON string
battle_round_update_request_instance = BattleRoundUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleRoundUpdateRequest.to_json())

# convert the object into a dict
battle_round_update_request_dict = battle_round_update_request_instance.to_dict()
# create an instance of BattleRoundUpdateRequest from a dict
battle_round_update_request_from_dict = BattleRoundUpdateRequest.from_dict(battle_round_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
