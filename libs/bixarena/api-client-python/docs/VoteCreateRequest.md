# VoteCreateRequest

The information used to create a new vote.

## Properties

| Name           | Type                                    | Description                             | Notes |
| -------------- | --------------------------------------- | --------------------------------------- | ----- |
| **battle_id**  | **str**                                 | Unique identifier (UUID) of the battle. |
| **preference** | [**VotePreference**](VotePreference.md) |                                         |

## Example

```python
from bixarena_api_client.models.vote_create_request import VoteCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of VoteCreateRequest from a JSON string
vote_create_request_instance = VoteCreateRequest.from_json(json)
# print the JSON string representation of the object
print(VoteCreateRequest.to_json())

# convert the object into a dict
vote_create_request_dict = vote_create_request_instance.to_dict()
# create an instance of VoteCreateRequest from a dict
vote_create_request_from_dict = VoteCreateRequest.from_dict(vote_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
