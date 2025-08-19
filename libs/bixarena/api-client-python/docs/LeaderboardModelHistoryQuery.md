# LeaderboardModelHistoryQuery

A query for retrieving historical leaderboard data for a model.

## Properties

| Name            | Type                                                    | Description                                          | Notes                                                     |
| --------------- | ------------------------------------------------------- | ---------------------------------------------------- | --------------------------------------------------------- |
| **page_number** | **int**                                                 | The page number.                                     | [optional] [default to 0]                                 |
| **page_size**   | **int**                                                 | The number of items in a single page.                | [optional] [default to 100]                               |
| **sort**        | [**LeaderboardHistorySort**](LeaderboardHistorySort.md) |                                                      | [optional] [default to LeaderboardHistorySort.CREATED_AT] |
| **direction**   | [**SortDirection**](SortDirection.md)                   |                                                      | [optional] [default to SortDirection.ASC]                 |
| **from_date**   | **date**                                                | Include only entries created on or after this date.  | [optional]                                                |
| **to_date**     | **date**                                                | Include only entries created on or before this date. | [optional]                                                |

## Example

```python
from bixarena_api_client_python.models.leaderboard_model_history_query import LeaderboardModelHistoryQuery

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardModelHistoryQuery from a JSON string
leaderboard_model_history_query_instance = LeaderboardModelHistoryQuery.from_json(json)
# print the JSON string representation of the object
print(LeaderboardModelHistoryQuery.to_json())

# convert the object into a dict
leaderboard_model_history_query_dict = leaderboard_model_history_query_instance.to_dict()
# create an instance of LeaderboardModelHistoryQuery from a dict
leaderboard_model_history_query_from_dict = LeaderboardModelHistoryQuery.from_dict(leaderboard_model_history_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
