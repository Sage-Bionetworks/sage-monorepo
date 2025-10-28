# BattlePage

A page of battles.

## Properties

| Name        | Type                                | Description                   | Notes |
| ----------- | ----------------------------------- | ----------------------------- | ----- |
| **battles** | [**List[Battle]**](Battle.md)       | List of battles in this page. |
| **page**    | [**PageMetadata**](PageMetadata.md) |                               |

## Example

```python
from bixarena_api_client.models.battle_page import BattlePage

# TODO update the JSON string below
json = "{}"
# create an instance of BattlePage from a JSON string
battle_page_instance = BattlePage.from_json(json)
# print the JSON string representation of the object
print(BattlePage.to_json())

# convert the object into a dict
battle_page_dict = battle_page_instance.to_dict()
# create an instance of BattlePage from a dict
battle_page_from_dict = BattlePage.from_dict(battle_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
