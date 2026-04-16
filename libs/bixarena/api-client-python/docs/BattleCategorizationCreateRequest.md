# BattleCategorizationCreateRequest

Request to manually categorize a battle.

## Properties

| Name           | Type                                                  | Description | Notes |
| -------------- | ----------------------------------------------------- | ----------- | ----- |
| **categories** | [**List[BiomedicalCategory]**](BiomedicalCategory.md) |             |
| **reason**     | **str**                                               |             |

## Example

```python
from bixarena_api_client.models.battle_categorization_create_request import BattleCategorizationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleCategorizationCreateRequest from a JSON string
battle_categorization_create_request_instance = BattleCategorizationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleCategorizationCreateRequest.to_json())

# convert the object into a dict
battle_categorization_create_request_dict = battle_categorization_create_request_instance.to_dict()
# create an instance of BattleCategorizationCreateRequest from a dict
battle_categorization_create_request_from_dict = BattleCategorizationCreateRequest.from_dict(battle_categorization_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
