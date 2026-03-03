# BattleSearchQuery

A battle search query.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**page_number** | **int** | The page number to return (0-indexed). | [default to 0]
**page_size** | **int** | The number of items to return in a single page. | [default to 100]
**sort** | [**BattleSort**](BattleSort.md) |  | [default to BattleSort.CREATED_AT]
**direction** | [**SortDirection**](SortDirection.md) |  | [default to SortDirection.ASC]
**user_id** | **str** | Filter by user ID. | [optional] 

## Example

```python
from bixarena_api_client.models.battle_search_query import BattleSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of BattleSearchQuery from a JSON string
battle_search_query_instance = BattleSearchQuery.from_json(json)
# print the JSON string representation of the object
print(BattleSearchQuery.to_json())

# convert the object into a dict
battle_search_query_dict = battle_search_query_instance.to_dict()
# create an instance of BattleSearchQuery from a dict
battle_search_query_from_dict = BattleSearchQuery.from_dict(battle_search_query_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


