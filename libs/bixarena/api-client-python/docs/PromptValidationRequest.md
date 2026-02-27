# PromptValidationRequest

The prompt text to validate for biomedical relevance.

## Properties

| Name       | Type    | Description                 | Notes |
| ---------- | ------- | --------------------------- | ----- |
| **prompt** | **str** | The prompt text to validate |

## Example

```python
from bixarena_api_client.models.prompt_validation_request import PromptValidationRequest

# TODO update the JSON string below
json = "{}"
# create an instance of PromptValidationRequest from a JSON string
prompt_validation_request_instance = PromptValidationRequest.from_json(json)
# print the JSON string representation of the object
print(PromptValidationRequest.to_json())

# convert the object into a dict
prompt_validation_request_dict = prompt_validation_request_instance.to_dict()
# create an instance of PromptValidationRequest from a dict
prompt_validation_request_from_dict = PromptValidationRequest.from_dict(prompt_validation_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
