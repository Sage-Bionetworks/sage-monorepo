# ModelErrorCreateRequest

Request to report a model error.

## Properties

| Name          | Type    | Description                                   | Notes      |
| ------------- | ------- | --------------------------------------------- | ---------- |
| **code**      | **int** | HTTP status code from the error response.     | [optional] |
| **message**   | **str** | Error message describing what went wrong.     |
| **battle_id** | **str** | Unique identifier (UUID) of the battle.       |
| **round_id**  | **str** | Unique identifier (UUID) of the battle round. |

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
