# ModelSearchQuery

A model search query with pagination and filtering options.

## Properties

| Name             | Type                                  | Description                                                                                   | Notes                                     |
| ---------------- | ------------------------------------- | --------------------------------------------------------------------------------------------- | ----------------------------------------- |
| **page_number**  | **int**                               | The page number.                                                                              | [optional] [default to 0]                 |
| **page_size**    | **int**                               | The number of items in a single page.                                                         | [optional] [default to 25]                |
| **sort**         | [**ModelSort**](ModelSort.md)         |                                                                                               | [optional] [default to ModelSort.NAME]    |
| **direction**    | [**SortDirection**](SortDirection.md) |                                                                                               | [optional] [default to SortDirection.ASC] |
| **search**       | **str**                               | Search by model name or slug (case-insensitive partial match).                                | [optional]                                |
| **active**       | **bool**                              | Filter by active status (true returns only active models; false only inactive; omit for all). | [optional]                                |
| **license**      | **str**                               | Filter by license type.                                                                       | [optional]                                |
| **organization** | **str**                               | Filter by organization name (case-insensitive partial match).                                 | [optional]                                |

## Example

```python
from bixarena_api_client.models.model_search_query import ModelSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ModelSearchQuery from a JSON string
model_search_query_instance = ModelSearchQuery.from_json(json)
# print the JSON string representation of the object
print(ModelSearchQuery.to_json())

# convert the object into a dict
model_search_query_dict = model_search_query_instance.to_dict()
# create an instance of ModelSearchQuery from a dict
model_search_query_from_dict = ModelSearchQuery.from_dict(model_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
