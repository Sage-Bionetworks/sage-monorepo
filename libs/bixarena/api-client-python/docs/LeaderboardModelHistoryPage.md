# LeaderboardModelHistoryPage

A page of historical leaderboard entries for a specific model.

## Properties

| Name               | Type                                                                  | Description                                 | Notes |
| ------------------ | --------------------------------------------------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                                                               | The page number.                            |
| **size**           | **int**                                                               | The number of items in a single page.       |
| **total_elements** | **int**                                                               | Total number of elements in the result set. |
| **total_pages**    | **int**                                                               | Total number of pages in the result set.    |
| **has_next**       | **bool**                                                              | Returns if there is a next page.            |
| **has_previous**   | **bool**                                                              | Returns if there is a previous page.        |
| **model_id**       | **str**                                                               | Identifier for the model                    |
| **model_name**     | **str**                                                               | Display name of the model                   |
| **history**        | [**List[HistoricalLeaderboardEntry]**](HistoricalLeaderboardEntry.md) | A list of historical leaderboard entries.   |

## Example

```python
from bixarena_api_client.models.leaderboard_model_history_page import LeaderboardModelHistoryPage

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardModelHistoryPage from a JSON string
leaderboard_model_history_page_instance = LeaderboardModelHistoryPage.from_json(json)
# print the JSON string representation of the object
print(LeaderboardModelHistoryPage.to_json())

# convert the object into a dict
leaderboard_model_history_page_dict = leaderboard_model_history_page_instance.to_dict()
# create an instance of LeaderboardModelHistoryPage from a dict
leaderboard_model_history_page_from_dict = LeaderboardModelHistoryPage.from_dict(leaderboard_model_history_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
