# LeaderboardSnapshot

A snapshot representing the state of a leaderboard at a specific point in time.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Unique identifier for this snapshot | 
**created_at** | **datetime** | When this snapshot was created | 
**entry_count** | **int** | Number of models in this snapshot | 
**description** | **str** | Optional description of this snapshot | [optional] 

## Example

```python
from bixarena_api_client.models.leaderboard_snapshot import LeaderboardSnapshot

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardSnapshot from a JSON string
leaderboard_snapshot_instance = LeaderboardSnapshot.from_json(json)
# print the JSON string representation of the object
print(LeaderboardSnapshot.to_json())

# convert the object into a dict
leaderboard_snapshot_dict = leaderboard_snapshot_instance.to_dict()
# create an instance of LeaderboardSnapshot from a dict
leaderboard_snapshot_from_dict = LeaderboardSnapshot.from_dict(leaderboard_snapshot_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


