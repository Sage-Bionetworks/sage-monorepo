# VotePage

A page of votes

## Properties

| Name     | Type                                | Description | Notes |
| -------- | ----------------------------------- | ----------- | ----- |
| **data** | [**List[Vote]**](Vote.md)           |             |
| **page** | [**PageMetadata**](PageMetadata.md) |             |

## Example

```python
from bixarena_api_client.models.vote_page import VotePage

# TODO update the JSON string below
json = "{}"
# create an instance of VotePage from a JSON string
vote_page_instance = VotePage.from_json(json)
# print the JSON string representation of the object
print(VotePage.to_json())

# convert the object into a dict
vote_page_dict = vote_page_instance.to_dict()
# create an instance of VotePage from a dict
vote_page_from_dict = VotePage.from_dict(vote_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
