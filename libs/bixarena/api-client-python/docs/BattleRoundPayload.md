# BattleRoundPayload

The request payload for a battle round.

## Properties

| Name               | Type                                  | Description | Notes      |
| ------------------ | ------------------------------------- | ----------- | ---------- |
| **prompt_message** | [**MessageCreate**](MessageCreate.md) |             | [optional] |
| **model1_message** | [**MessageCreate**](MessageCreate.md) |             | [optional] |
| **model2_message** | [**MessageCreate**](MessageCreate.md) |             | [optional] |

## Example

```python
from bixarena_api_client.models.battle_round_payload import BattleRoundPayload

# TODO update the JSON string below
json = "{}"
# create an instance of BattleRoundPayload from a JSON string
battle_round_payload_instance = BattleRoundPayload.from_json(json)
# print the JSON string representation of the object
print(BattleRoundPayload.to_json())

# convert the object into a dict
battle_round_payload_dict = battle_round_payload_instance.to_dict()
# create an instance of BattleRoundPayload from a dict
battle_round_payload_from_dict = BattleRoundPayload.from_dict(battle_round_payload_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
