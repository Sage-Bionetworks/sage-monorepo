# UserStats

Statistics about a user's participation in battles.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**total_battles** | **int** | Total number of battles the user has participated in (as arbiter) | 
**completed_battles** | **int** | Number of battles that have been completed (endedAt is set) | 
**active_battles** | **int** | Number of battles currently in progress (endedAt is null) | 
**first_battle_at** | **datetime** | Timestamp of the user&#39;s first battle | [optional] 
**latest_battle_at** | **datetime** | Timestamp of the user&#39;s most recent battle | [optional] 

## Example

```python
from bixarena_api_client.models.user_stats import UserStats

# TODO update the JSON string below
json = "{}"
# create an instance of UserStats from a JSON string
user_stats_instance = UserStats.from_json(json)
# print the JSON string representation of the object
print(UserStats.to_json())

# convert the object into a dict
user_stats_dict = user_stats_instance.to_dict()
# create an instance of UserStats from a dict
user_stats_from_dict = UserStats.from_dict(user_stats_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


