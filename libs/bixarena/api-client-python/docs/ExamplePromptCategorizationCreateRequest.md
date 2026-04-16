# ExamplePromptCategorizationCreateRequest

Request to manually categorize an example prompt.

## Properties

| Name           | Type                                                  | Description                            | Notes      |
| -------------- | ----------------------------------------------------- | -------------------------------------- | ---------- |
| **categories** | [**List[BiomedicalCategory]**](BiomedicalCategory.md) |                                        |
| **reason**     | **str**                                               | Reason for the categorization decision | [optional] |

## Example

```python
from bixarena_api_client.models.example_prompt_categorization_create_request import ExamplePromptCategorizationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptCategorizationCreateRequest from a JSON string
example_prompt_categorization_create_request_instance = ExamplePromptCategorizationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptCategorizationCreateRequest.to_json())

# convert the object into a dict
example_prompt_categorization_create_request_dict = example_prompt_categorization_create_request_instance.to_dict()
# create an instance of ExamplePromptCategorizationCreateRequest from a dict
example_prompt_categorization_create_request_from_dict = ExamplePromptCategorizationCreateRequest.from_dict(example_prompt_categorization_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
