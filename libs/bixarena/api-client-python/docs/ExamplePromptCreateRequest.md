# ExamplePromptCreateRequest

The information used to create an example prompt. Newly created prompts are inactive until a reviewer publishes them via PATCH. AI auto-categorization runs asynchronously after creation; admins can manually override later via the categorization endpoints.

## Properties

| Name         | Type                                              | Description                   | Notes |
| ------------ | ------------------------------------------------- | ----------------------------- | ----- |
| **question** | **str**                                           | The biomedical question text. |
| **source**   | [**ExamplePromptSource**](ExamplePromptSource.md) |                               |

## Example

```python
from bixarena_api_client.models.example_prompt_create_request import ExamplePromptCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptCreateRequest from a JSON string
example_prompt_create_request_instance = ExamplePromptCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptCreateRequest.to_json())

# convert the object into a dict
example_prompt_create_request_dict = example_prompt_create_request_instance.to_dict()
# create an instance of ExamplePromptCreateRequest from a dict
example_prompt_create_request_from_dict = ExamplePromptCreateRequest.from_dict(example_prompt_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
