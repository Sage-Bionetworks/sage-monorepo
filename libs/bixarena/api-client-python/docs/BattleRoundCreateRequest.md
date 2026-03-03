# BattleRoundCreateRequest

Payload to create a new battle round with the user prompt.

## Properties

| Name               | Type                                  | Description | Notes |
| ------------------ | ------------------------------------- | ----------- | ----- |
| **prompt_message** | [**MessageCreate**](MessageCreate.md) |             |

## Example

```python
from bixarena_api_client.models.battle_round_create_request import BattleRoundCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleRoundCreateRequest from a JSON string
battle_round_create_request_instance = BattleRoundCreateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleRoundCreateRequest.to_json())

# convert the object into a dict
battle_round_create_request_dict = battle_round_create_request_instance.to_dict()
# create an instance of BattleRoundCreateRequest from a dict
battle_round_create_request_from_dict = BattleRoundCreateRequest.from_dict(battle_round_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
