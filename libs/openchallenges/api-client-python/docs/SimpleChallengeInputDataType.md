# SimpleChallengeInputDataType

A simple challenge input data type.

## Properties

| Name     | Type    | Description                                           | Notes |
| -------- | ------- | ----------------------------------------------------- | ----- |
| **id**   | **int** | The unique identifier of a challenge input data type. |
| **slug** | **str** | The slug of the challenge input data type.            |
| **name** | **str** | The name of the challenge input data type.            |

## Example

```python
from openchallenges_client.models.simple_challenge_input_data_type import SimpleChallengeInputDataType

# TODO update the JSON string below
json = "{}"
# create an instance of SimpleChallengeInputDataType from a JSON string
simple_challenge_input_data_type_instance = SimpleChallengeInputDataType.from_json(json)
# print the JSON string representation of the object
print SimpleChallengeInputDataType.to_json()

# convert the object into a dict
simple_challenge_input_data_type_dict = simple_challenge_input_data_type_instance.to_dict()
# create an instance of SimpleChallengeInputDataType from a dict
simple_challenge_input_data_type_form_dict = simple_challenge_input_data_type.from_dict(simple_challenge_input_data_type_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
