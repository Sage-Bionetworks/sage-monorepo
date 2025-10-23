# VoteSearchQuery

A vote search query.

## Properties

| Name            | Type                                    | Description                                     | Notes                            |
| --------------- | --------------------------------------- | ----------------------------------------------- | -------------------------------- |
| **page_number** | **int**                                 | The page number to return (0-indexed).          | [default to 0]                   |
| **page_size**   | **int**                                 | The number of items to return in a single page. | [default to 100]                 |
| **sort**        | [**VoteSort**](VoteSort.md)             |                                                 | [default to VoteSort.CREATED_AT] |
| **direction**   | [**SortDirection**](SortDirection.md)   |                                                 | [default to SortDirection.ASC]   |
| **preference**  | [**VotePreference**](VotePreference.md) |                                                 | [optional]                       |

## Example

```python
from bixarena_api_client.models.vote_search_query import VoteSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of VoteSearchQuery from a JSON string
vote_search_query_instance = VoteSearchQuery.from_json(json)
# print the JSON string representation of the object
print(VoteSearchQuery.to_json())

# convert the object into a dict
vote_search_query_dict = vote_search_query_instance.to_dict()
# create an instance of VoteSearchQuery from a dict
vote_search_query_from_dict = VoteSearchQuery.from_dict(vote_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
