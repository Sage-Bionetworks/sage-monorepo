# EvaluationCreateRequest

The information used to create a new evaluation.

## Properties

| Name        | Type                                          | Description | Notes |
| ----------- | --------------------------------------------- | ----------- | ----- |
| **outcome** | [**EvaluationOutcome**](EvaluationOutcome.md) |             |

## Example

```python
from bixarena_api_client.models.evaluation_create_request import EvaluationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of EvaluationCreateRequest from a JSON string
evaluation_create_request_instance = EvaluationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(EvaluationCreateRequest.to_json())

# convert the object into a dict
evaluation_create_request_dict = evaluation_create_request_instance.to_dict()
# create an instance of EvaluationCreateRequest from a dict
evaluation_create_request_from_dict = EvaluationCreateRequest.from_dict(evaluation_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
