# Model

A model entity.

## Properties

| Name           | Type         | Description                                | Notes      |
| -------------- | ------------ | ------------------------------------------ | ---------- |
| **id**         | **str**      | Unique identifier (UUID) of the model.     |
| **slug**       | **str**      | URL-friendly unique slug for the model.    |
| **name**       | **str**      | Human-readable name of the model.          |
| **license**    | **str**      | License under which the model is released. | [optional] |
| **active**     | **bool**     | Whether the model is active/visible.       |
| **created_at** | **datetime** | When the model was created.                |
| **updated_at** | **datetime** | When the model was last updated.           |

## Example

```python
from bixarena_api_client.models.model import Model

# TODO update the JSON string below
json = "{}"
# create an instance of Model from a JSON string
model_instance = Model.from_json(json)
# print the JSON string representation of the object
print(Model.to_json())

# convert the object into a dict
model_dict = model_instance.to_dict()
# create an instance of Model from a dict
model_from_dict = Model.from_dict(model_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
