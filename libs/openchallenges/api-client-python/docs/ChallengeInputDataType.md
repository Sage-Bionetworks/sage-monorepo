# ChallengeInputDataType

A challenge input data type.

## Properties

| Name           | Type         | Description                                           | Notes |
| -------------- | ------------ | ----------------------------------------------------- | ----- |
| **id**         | **int**      | The unique identifier of a challenge input data type. |
| **slug**       | **str**      | The slug of the challenge input data type.            |
| **name**       | **str**      | The name of the challenge input data type.            |
| **created_at** | **datetime** |                                                       |
| **updated_at** | **datetime** |                                                       |

## Example

```python
from openchallenges_client.models.challenge_input_data_type import ChallengeInputDataType

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeInputDataType from a JSON string
challenge_input_data_type_instance = ChallengeInputDataType.from_json(json)
# print the JSON string representation of the object
print ChallengeInputDataType.to_json()

# convert the object into a dict
challenge_input_data_type_dict = challenge_input_data_type_instance.to_dict()
# create an instance of ChallengeInputDataType from a dict
challenge_input_data_type_form_dict = challenge_input_data_type.from_dict(challenge_input_data_type_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
