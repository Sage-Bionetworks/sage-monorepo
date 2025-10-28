# LeaderboardEntry

A single entry in a leaderboard representing a model's performance.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Unique identifier for this leaderboard entry | 
**model_id** | **str** | Identifier for the model | 
**model_name** | **str** | Display name of the model | 
**license** | **str** | License type of the model | 
**bt_score** | **float** | Primary scoring metric (higher is better) | 
**vote_count** | **int** | Number of votes/evaluations | 
**rank** | **int** | Current rank position (1-based) | 
**created_at** | **datetime** | When this entry was created | 

## Example

```python
from bixarena_api_client.models.leaderboard_entry import LeaderboardEntry

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardEntry from a JSON string
leaderboard_entry_instance = LeaderboardEntry.from_json(json)
# print the JSON string representation of the object
print(LeaderboardEntry.to_json())

# convert the object into a dict
leaderboard_entry_dict = leaderboard_entry_instance.to_dict()
# create an instance of LeaderboardEntry from a dict
leaderboard_entry_from_dict = LeaderboardEntry.from_dict(leaderboard_entry_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


