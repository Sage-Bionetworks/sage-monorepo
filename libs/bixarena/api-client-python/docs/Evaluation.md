# Evaluation

An evaluation entity representing a user's assessment in a battle between two AI models.

## Properties

| Name                 | Type                                          | Description                                                   | Notes              |
| -------------------- | --------------------------------------------- | ------------------------------------------------------------- | ------------------ |
| **id**               | **str**                                       | The unique identifier of the evaluation                       |
| **outcome**          | [**EvaluationOutcome**](EvaluationOutcome.md) |                                                               |
| **created_at**       | **datetime**                                  | Timestamp when the entity was created.                        |
| **is_valid**         | **bool**                                      | Indicates whether the resource passed server-side validation. | [default to False] |
| **validation_error** | **str**                                       | Short validation error message or reason                      | [optional]         |

## Example

```python
from bixarena_api_client.models.evaluation import Evaluation

# TODO update the JSON string below
json = "{}"
# create an instance of Evaluation from a JSON string
evaluation_instance = Evaluation.from_json(json)
# print the JSON string representation of the object
print(Evaluation.to_json())

# convert the object into a dict
evaluation_dict = evaluation_instance.to_dict()
# create an instance of Evaluation from a dict
evaluation_from_dict = Evaluation.from_dict(evaluation_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
