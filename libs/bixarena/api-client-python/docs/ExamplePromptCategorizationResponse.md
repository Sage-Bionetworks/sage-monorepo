# ExamplePromptCategorizationResponse

The result of a categorization run for an example prompt.

## Properties

| Name               | Type                                                  | Description                                     | Notes      |
| ------------------ | ----------------------------------------------------- | ----------------------------------------------- | ---------- |
| **id**             | **str**                                               |                                                 |
| **prompt_id**      | **str**                                               |                                                 |
| **categories**     | [**List[BiomedicalCategory]**](BiomedicalCategory.md) |                                                 |
| **method**         | **str**                                               |                                                 |
| **categorized_by** | **str**                                               | User ID of the categorizer. Null for AI runs.   | [optional] |
| **reason**         | **str**                                               | Human override reason. Always null for AI runs. | [optional] |
| **created_at**     | **datetime**                                          |                                                 |

## Example

```python
from bixarena_api_client.models.example_prompt_categorization_response import ExamplePromptCategorizationResponse

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptCategorizationResponse from a JSON string
example_prompt_categorization_response_instance = ExamplePromptCategorizationResponse.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptCategorizationResponse.to_json())

# convert the object into a dict
example_prompt_categorization_response_dict = example_prompt_categorization_response_instance.to_dict()
# create an instance of ExamplePromptCategorizationResponse from a dict
example_prompt_categorization_response_from_dict = ExamplePromptCategorizationResponse.from_dict(example_prompt_categorization_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
