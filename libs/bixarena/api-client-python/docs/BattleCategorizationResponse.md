# BattleCategorizationResponse

The result of a categorization run for a battle.

## Properties

| Name               | Type                                                  | Description                                     | Notes      |
| ------------------ | ----------------------------------------------------- | ----------------------------------------------- | ---------- |
| **id**             | **str**                                               |                                                 |
| **battle_id**      | **str**                                               |                                                 |
| **categories**     | [**List[BiomedicalCategory]**](BiomedicalCategory.md) |                                                 |
| **method**         | **str**                                               |                                                 |
| **categorized_by** | **str**                                               | User ID of the categorizer. Null for AI runs.   | [optional] |
| **reason**         | **str**                                               | Human override reason. Always null for AI runs. | [optional] |
| **created_at**     | **datetime**                                          |                                                 |

## Example

```python
from bixarena_api_client.models.battle_categorization_response import BattleCategorizationResponse

# TODO update the JSON string below
json = "{}"
# create an instance of BattleCategorizationResponse from a JSON string
battle_categorization_response_instance = BattleCategorizationResponse.from_json(json)
# print the JSON string representation of the object
print(BattleCategorizationResponse.to_json())

# convert the object into a dict
battle_categorization_response_dict = battle_categorization_response_instance.to_dict()
# create an instance of BattleCategorizationResponse from a dict
battle_categorization_response_from_dict = BattleCategorizationResponse.from_dict(battle_categorization_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
