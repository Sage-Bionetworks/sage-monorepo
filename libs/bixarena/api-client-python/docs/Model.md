# Model

A model entity.

## Properties

| Name               | Type         | Description                                         | Notes      |
| ------------------ | ------------ | --------------------------------------------------- | ---------- |
| **id**             | **str**      | Unique identifier (UUID) of the model.              |
| **slug**           | **str**      | URL-friendly unique slug for the model.             |
| **alias**          | **str**      | Alternative name or alias for the model.            | [optional] |
| **name**           | **str**      | Human-readable name of the model.                   |
| **organization**   | **str**      | Organization that developed or maintains the model. | [optional] |
| **license**        | **str**      | Whether the model is open-source or commercial.     |
| **active**         | **bool**     | Whether the model is active/visible.                |
| **external_link**  | **str**      | External URL with more information about the model. |
| **description**    | **str**      | Detailed description of the model.                  | [optional] |
| **api_model_name** | **str**      | The model name used for API calls.                  |
| **api_base**       | **str**      | Base URL for the model API.                         |
| **created_at**     | **datetime** | When the model was created.                         |
| **updated_at**     | **datetime** | When the model was last updated.                    |

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
