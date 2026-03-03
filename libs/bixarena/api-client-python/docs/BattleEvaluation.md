# BattleEvaluation

A battle evaluation describing the outcome of a matchup.

## Properties

| Name           | Type                                                      | Description                                    | Notes |
| -------------- | --------------------------------------------------------- | ---------------------------------------------- | ----- |
| **id**         | **str**                                                   | The unique identifier of the battle evaluation |
| **battle_id**  | **str**                                                   | Unique identifier (UUID) of the battle.        |
| **outcome**    | [**BattleEvaluationOutcome**](BattleEvaluationOutcome.md) |                                                |
| **created_at** | **datetime**                                              | Timestamp when the entity was created.         |

## Example

```python
from bixarena_api_client.models.battle_evaluation import BattleEvaluation

# TODO update the JSON string below
json = "{}"
# create an instance of BattleEvaluation from a JSON string
battle_evaluation_instance = BattleEvaluation.from_json(json)
# print the JSON string representation of the object
print(BattleEvaluation.to_json())

# convert the object into a dict
battle_evaluation_dict = battle_evaluation_instance.to_dict()
# create an instance of BattleEvaluation from a dict
battle_evaluation_from_dict = BattleEvaluation.from_dict(battle_evaluation_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
