# ExamplePromptUpdateRequest

The information used to update an example prompt. Only fields present in the request are modified.

## Properties

| Name         | Type                                              | Description                                                      | Notes      |
| ------------ | ------------------------------------------------- | ---------------------------------------------------------------- | ---------- |
| **question** | **str**                                           | The biomedical question text.                                    | [optional] |
| **source**   | [**ExamplePromptSource**](ExamplePromptSource.md) |                                                                  | [optional] |
| **active**   | **bool**                                          | Whether this example prompt is currently active/visible for use. | [optional] |

## Example

```python
from bixarena_api_client.models.example_prompt_update_request import ExamplePromptUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptUpdateRequest from a JSON string
example_prompt_update_request_instance = ExamplePromptUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptUpdateRequest.to_json())

# convert the object into a dict
example_prompt_update_request_dict = example_prompt_update_request_instance.to_dict()
# create an instance of ExamplePromptUpdateRequest from a dict
example_prompt_update_request_from_dict = ExamplePromptUpdateRequest.from_dict(example_prompt_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
