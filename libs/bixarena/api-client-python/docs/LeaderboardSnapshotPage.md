# LeaderboardSnapshotPage

A page of leaderboard snapshots.

## Properties

| Name               | Type                                                    | Description                                 | Notes |
| ------------------ | ------------------------------------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                                                 | The page number.                            |
| **size**           | **int**                                                 | The number of items in a single page.       |
| **total_elements** | **int**                                                 | Total number of elements in the result set. |
| **total_pages**    | **int**                                                 | Total number of pages in the result set.    |
| **has_next**       | **bool**                                                | Returns if there is a next page.            |
| **has_previous**   | **bool**                                                | Returns if there is a previous page.        |
| **snapshots**      | [**List[LeaderboardSnapshot]**](LeaderboardSnapshot.md) | A list of leaderboard snapshots.            |

## Example

```python
from bixarena_api_client_python.models.leaderboard_snapshot_page import LeaderboardSnapshotPage

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardSnapshotPage from a JSON string
leaderboard_snapshot_page_instance = LeaderboardSnapshotPage.from_json(json)
# print the JSON string representation of the object
print(LeaderboardSnapshotPage.to_json())

# convert the object into a dict
leaderboard_snapshot_page_dict = leaderboard_snapshot_page_instance.to_dict()
# create an instance of LeaderboardSnapshotPage from a dict
leaderboard_snapshot_page_from_dict = LeaderboardSnapshotPage.from_dict(leaderboard_snapshot_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
