# BattleRound

A battle round containing the IDs of prompt and responses.

## Properties

| Name                     | Type         | Description                                   | Notes      |
| ------------------------ | ------------ | --------------------------------------------- | ---------- |
| **id**                   | **str**      | Unique identifier (UUID) of the battle round. |
| **battle_id**            | **str**      | Unique identifier (UUID) of the battle.       |
| **prompt_message_id**    | **str**      | Unique identifier (UUID) of the message.      | [optional] |
| **response1_message_id** | **str**      | Unique identifier (UUID) of the message.      | [optional] |
| **response2_message_id** | **str**      | Unique identifier (UUID) of the message.      | [optional] |
| **created_at**           | **datetime** | Timestamp when the entity was created.        |
| **updated_at**           | **datetime** | Timestamp when the entity was last updated.   |

## Example

```python
from bixarena_api_client.models.battle_round import BattleRound

# TODO update the JSON string below
json = "{}"
# create an instance of BattleRound from a JSON string
battle_round_instance = BattleRound.from_json(json)
# print the JSON string representation of the object
print(BattleRound.to_json())

# convert the object into a dict
battle_round_dict = battle_round_instance.to_dict()
# create an instance of BattleRound from a dict
battle_round_from_dict = BattleRound.from_dict(battle_round_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
