# ModelError

A model error entity representing a failure that occurred during model interaction.

## Properties

| Name              | Type         | Description                                                                            | Notes      |
| ----------------- | ------------ | -------------------------------------------------------------------------------------- | ---------- |
| **id**            | **str**      | Unique identifier (UUID) of the model error record.                                    |
| **model_id**      | **str**      | UUID of an AI model.                                                                   |
| **error_code**    | **int**      | HTTP status code from the API response (400, 401, 402, 403, 408, 429, 502, 503, etc.). | [optional] |
| **error_message** | **str**      | The error message from the API or exception with full details.                         |
| **battle_id**     | **str**      | Unique identifier (UUID) of the battle.                                                | [optional] |
| **round_id**      | **str**      | Unique identifier (UUID) of the battle round.                                          | [optional] |
| **created_at**    | **datetime** | Timestamp when the entity was created.                                                 |

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
