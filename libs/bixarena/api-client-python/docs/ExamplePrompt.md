# ExamplePrompt

A self-contained example prompt for biomedical question answering.

## Properties

| Name           | Type                                              | Description                                                      | Notes |
| -------------- | ------------------------------------------------- | ---------------------------------------------------------------- | ----- |
| **id**         | **str**                                           | The unique identifier of the example prompt.                     |
| **question**   | **str**                                           | The biomedical question text.                                    |
| **source**     | [**ExamplePromptSource**](ExamplePromptSource.md) |                                                                  |
| **active**     | **bool**                                          | Whether this example prompt is currently active/visible for use. |
| **created_at** | **datetime**                                      | When the example prompt was created.                             |

## Example

```python
from bixarena_api_client.models.example_prompt import ExamplePrompt

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePrompt from a JSON string
example_prompt_instance = ExamplePrompt.from_json(json)
# print the JSON string representation of the object
print(ExamplePrompt.to_json())

# convert the object into a dict
example_prompt_dict = example_prompt_instance.to_dict()
# create an instance of ExamplePrompt from a dict
example_prompt_from_dict = ExamplePrompt.from_dict(example_prompt_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
