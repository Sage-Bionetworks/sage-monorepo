# Vote

A vote entity representing a user's preference in a battle between two AI models.

## Properties

| Name           | Type                                    | Description                                       | Notes |
| -------------- | --------------------------------------- | ------------------------------------------------- | ----- |
| **id**         | **str**                                 | The unique identifier of the vote                 |
| **battle_id**  | **str**                                 | The identifier of the battle this vote belongs to |
| **preference** | [**VotePreference**](VotePreference.md) |                                                   |
| **created_at** | **datetime**                            | Timestamp when the entity was created.            |

## Example

```python
from bixarena_api_client.models.vote import Vote

# TODO update the JSON string below
json = "{}"
# create an instance of Vote from a JSON string
vote_instance = Vote.from_json(json)
# print the JSON string representation of the object
print(Vote.to_json())

# convert the object into a dict
vote_dict = vote_instance.to_dict()
# create an instance of Vote from a dict
vote_from_dict = Vote.from_dict(vote_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
