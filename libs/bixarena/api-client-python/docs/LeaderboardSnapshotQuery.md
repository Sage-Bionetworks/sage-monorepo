# LeaderboardSnapshotQuery

A query for retrieving leaderboard snapshots.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**page_number** | **int** | The page number. | [optional] [default to 0]
**page_size** | **int** | The number of items in a single page. | [optional] [default to 100]
**sort** | [**LeaderboardSnapshotSort**](LeaderboardSnapshotSort.md) |  | [optional] [default to LeaderboardSnapshotSort.CREATED_AT]
**direction** | [**SortDirection**](SortDirection.md) |  | [optional] [default to SortDirection.ASC]

## Example

```python
from bixarena_api_client.models.leaderboard_snapshot_query import LeaderboardSnapshotQuery

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardSnapshotQuery from a JSON string
leaderboard_snapshot_query_instance = LeaderboardSnapshotQuery.from_json(json)
# print the JSON string representation of the object
print(LeaderboardSnapshotQuery.to_json())

# convert the object into a dict
leaderboard_snapshot_query_dict = leaderboard_snapshot_query_instance.to_dict()
# create an instance of LeaderboardSnapshotQuery from a dict
leaderboard_snapshot_query_from_dict = LeaderboardSnapshotQuery.from_dict(leaderboard_snapshot_query_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


