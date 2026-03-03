# ModelError

Record of a model error that occurred during interaction.

## Properties

| Name           | Type         | Description                                   | Notes      |
| -------------- | ------------ | --------------------------------------------- | ---------- |
| **id**         | **str**      | Unique identifier of the model error record.  |
| **model_id**   | **str**      | UUID of an AI model.                          |
| **code**       | **int**      | HTTP status code from the error response.     | [optional] |
| **message**    | **str**      | Error message describing what went wrong.     |
| **battle_id**  | **str**      | Unique identifier (UUID) of the battle.       |
| **round_id**   | **str**      | Unique identifier (UUID) of the battle round. |
| **created_at** | **datetime** | Timestamp when the entity was created.        |

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
