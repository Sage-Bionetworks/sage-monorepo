# BattleEvaluation

A battle evaluation entity representing a user's assessment in a battle between two AI models.

## Properties

| Name                 | Type                                                      | Description                                                   | Notes              |
| -------------------- | --------------------------------------------------------- | ------------------------------------------------------------- | ------------------ |
| **id**               | **str**                                                   | The unique identifier of the battle evaluation                |
| **outcome**          | [**BattleEvaluationOutcome**](BattleEvaluationOutcome.md) |                                                               |
| **created_at**       | **datetime**                                              | Timestamp when the entity was created.                        |
| **is_valid**         | **bool**                                                  | Indicates whether the resource passed server-side validation. | [default to False] |
| **validation_error** | **str**                                                   | Short validation error message or reason                      | [optional]         |

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
