# LeaderboardEntryPage

A page of leaderboard entries.

## Properties

| Name               | Type                                              | Description                                 | Notes |
| ------------------ | ------------------------------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                                           | The page number.                            |
| **size**           | **int**                                           | The number of items in a single page.       |
| **total_elements** | **int**                                           | Total number of elements in the result set. |
| **total_pages**    | **int**                                           | Total number of pages in the result set.    |
| **has_next**       | **bool**                                          | Returns if there is a next page.            |
| **has_previous**   | **bool**                                          | Returns if there is a previous page.        |
| **updated_at**     | **datetime**                                      | When this leaderboard was last updated      |
| **snapshot_id**    | **str**                                           | Identifier for this snapshot/timepoint      |
| **entries**        | [**List[LeaderboardEntry]**](LeaderboardEntry.md) | A list of leaderboard entries.              |

## Example

```python
from bixarena_api_client.models.leaderboard_entry_page import LeaderboardEntryPage

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardEntryPage from a JSON string
leaderboard_entry_page_instance = LeaderboardEntryPage.from_json(json)
# print the JSON string representation of the object
print(LeaderboardEntryPage.to_json())

# convert the object into a dict
leaderboard_entry_page_dict = leaderboard_entry_page_instance.to_dict()
# create an instance of LeaderboardEntryPage from a dict
leaderboard_entry_page_from_dict = LeaderboardEntryPage.from_dict(leaderboard_entry_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
