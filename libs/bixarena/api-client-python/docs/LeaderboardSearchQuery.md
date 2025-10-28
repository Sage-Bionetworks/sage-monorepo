# LeaderboardSearchQuery

A leaderboard search query with pagination and filtering options.

## Properties

| Name            | Type                                      | Description                                            | Notes                                        |
| --------------- | ----------------------------------------- | ------------------------------------------------------ | -------------------------------------------- |
| **page_number** | **int**                                   | The page number.                                       | [optional] [default to 0]                    |
| **page_size**   | **int**                                   | The number of items in a single page.                  | [optional] [default to 100]                  |
| **sort**        | [**LeaderboardSort**](LeaderboardSort.md) |                                                        | [optional] [default to LeaderboardSort.RANK] |
| **direction**   | [**SortDirection**](SortDirection.md)     |                                                        | [optional] [default to SortDirection.ASC]    |
| **search**      | **str**                                   | Search by model name (case-insensitive partial match). | [optional]                                   |
| **snapshot_id** | **str**                                   | Get a specific historical snapshot instead of latest.  | [optional]                                   |

## Example

```python
from bixarena_api_client.models.leaderboard_search_query import LeaderboardSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardSearchQuery from a JSON string
leaderboard_search_query_instance = LeaderboardSearchQuery.from_json(json)
# print the JSON string representation of the object
print(LeaderboardSearchQuery.to_json())

# convert the object into a dict
leaderboard_search_query_dict = leaderboard_search_query_instance.to_dict()
# create an instance of LeaderboardSearchQuery from a dict
leaderboard_search_query_from_dict = LeaderboardSearchQuery.from_dict(leaderboard_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
