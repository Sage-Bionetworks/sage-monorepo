# Battle

A battle entity representing a comparison between two AI models.

## Properties

| Name           | Type         | Description                                        | Notes      |
| -------------- | ------------ | -------------------------------------------------- | ---------- |
| **id**         | **str**      | Unique identifier (UUID) of the battle.            |
| **title**      | **str**      | Title of the battle.                               | [optional] |
| **user_id**    | **str**      | UUID of the user who initiated this battle.        |
| **model_aid**  | **str**      | UUID of model A in the battle.                     |
| **model_bid**  | **str**      | UUID of model B in the battle.                     |
| **created_at** | **datetime** | Timestamp when the battle was created.             |
| **ended_at**   | **datetime** | Timestamp when the battle ended (null if ongoing). | [optional] |

## Example

```python
from bixarena_api_client.models.battle import Battle

# TODO update the JSON string below
json = "{}"
# create an instance of Battle from a JSON string
battle_instance = Battle.from_json(json)
# print the JSON string representation of the object
print(Battle.to_json())

# convert the object into a dict
battle_dict = battle_instance.to_dict()
# create an instance of Battle from a dict
battle_from_dict = Battle.from_dict(battle_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
