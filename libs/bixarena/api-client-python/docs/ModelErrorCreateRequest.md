# ModelErrorCreateRequest

Request body for reporting a model error from the Gradio app. This enables tracking model failures, monitoring error rates, and potentially auto-disabling unreliable models.

## Properties

| Name              | Type    | Description                                                                                                                                | Notes      |
| ----------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ---------- |
| **error_code**    | **int** | HTTP status code from the API response (400, 401, 402, 403, 408, 429, 502, 503). May be null for network errors or client-side exceptions. | [optional] |
| **error_message** | **str** | The error message from the API or exception with full details                                                                              |
| **battle_id**     | **str** | The battle ID (UUID) if the error occurred during a battle (for tracking impact)                                                           | [optional] |
| **round_id**      | **str** | The round ID (UUID) if the error occurred during a specific round                                                                          | [optional] |

## Example

```python
from bixarena_api_client.models.model_error_create_request import ModelErrorCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ModelErrorCreateRequest from a JSON string
model_error_create_request_instance = ModelErrorCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ModelErrorCreateRequest.to_json())

# convert the object into a dict
model_error_create_request_dict = model_error_create_request_instance.to_dict()
# create an instance of ModelErrorCreateRequest from a dict
model_error_create_request_from_dict = ModelErrorCreateRequest.from_dict(model_error_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
