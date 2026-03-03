# ExamplePromptSearchQuery

An example prompt search query with pagination and filtering options.

## Properties

| Name            | Type                                              | Description                                                                                    | Notes                                                |
| --------------- | ------------------------------------------------- | ---------------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| **page_number** | **int**                                           | The page number.                                                                               | [optional] [default to 0]                            |
| **page_size**   | **int**                                           | The number of items in a single page.                                                          | [optional] [default to 25]                           |
| **sort**        | [**ExamplePromptSort**](ExamplePromptSort.md)     |                                                                                                | [optional] [default to ExamplePromptSort.CREATED_AT] |
| **direction**   | [**SortDirection**](SortDirection.md)             |                                                                                                | [optional] [default to SortDirection.ASC]            |
| **source**      | [**ExamplePromptSource**](ExamplePromptSource.md) |                                                                                                | [optional]                                           |
| **active**      | **bool**                                          | Filter by active status (true returns only active prompts; false only inactive; omit for all). | [optional]                                           |
| **search**      | **str**                                           | Search by question content (case-insensitive partial match).                                   | [optional]                                           |

## Example

```python
from bixarena_api_client.models.example_prompt_search_query import ExamplePromptSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptSearchQuery from a JSON string
example_prompt_search_query_instance = ExamplePromptSearchQuery.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptSearchQuery.to_json())

# convert the object into a dict
example_prompt_search_query_dict = example_prompt_search_query_instance.to_dict()
# create an instance of ExamplePromptSearchQuery from a dict
example_prompt_search_query_from_dict = ExamplePromptSearchQuery.from_dict(example_prompt_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
