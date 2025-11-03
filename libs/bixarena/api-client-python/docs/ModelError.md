# ModelError

A model error entity representing a failure that occurred during model interaction. Used for monitoring error rates and potentially auto-disabling unreliable models.

## Properties

| Name              | Type         | Description                                                        | Notes      |
| ----------------- | ------------ | ------------------------------------------------------------------ | ---------- |
| **id**            | **str**      | Unique identifier (UUID) of the model error record.                |
| **model_id**      | **str**      | The ID of the model that experienced the error.                    |
| **error_code**    | **int**      | HTTP status code from the API response.                            | [optional] |
| **error_message** | **str**      | The error message from the API or exception with full details.     |
| **battle_id**     | **str**      | The battle ID (UUID) if the error occurred during a battle.        | [optional] |
| **round_id**      | **str**      | The round ID (UUID) if the error occurred during a specific round. | [optional] |
| **created_at**    | **datetime** | When the error was reported by the Gradio app.                     |

## Example

```python
from bixarena_api_client.models.model_error import ModelError

# TODO update the JSON string below
json = "{}"
# create an instance of ModelError from a JSON string
model_error_instance = ModelError.from_json(json)
# print the JSON string representation of the object
print(ModelError.to_json())

# convert the object into a dict
model_error_dict = model_error_instance.to_dict()
# create an instance of ModelError from a dict
model_error_from_dict = ModelError.from_dict(model_error_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
