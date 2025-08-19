# LeaderboardListInner

## Properties

| Name            | Type         | Description                                   | Notes |
| --------------- | ------------ | --------------------------------------------- | ----- |
| **id**          | **str**      | Unique identifier for the leaderboard         |
| **name**        | **str**      | Display name for the leaderboard              |
| **description** | **str**      | Description of what this leaderboard measures |
| **updated_at**  | **datetime** | When this leaderboard was last updated        |

## Example

```python
from bixarena_api_client_python.models.leaderboard_list_inner import LeaderboardListInner

# TODO update the JSON string below
json = "{}"
# create an instance of LeaderboardListInner from a JSON string
leaderboard_list_inner_instance = LeaderboardListInner.from_json(json)
# print the JSON string representation of the object
print(LeaderboardListInner.to_json())

# convert the object into a dict
leaderboard_list_inner_dict = leaderboard_list_inner_instance.to_dict()
# create an instance of LeaderboardListInner from a dict
leaderboard_list_inner_from_dict = LeaderboardListInner.from_dict(leaderboard_list_inner_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
