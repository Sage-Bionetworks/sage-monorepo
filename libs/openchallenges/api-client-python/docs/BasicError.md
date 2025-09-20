# BasicError

Problem details (tools.ietf.org/html/rfc7807)

## Properties

| Name       | Type    | Description                                                             | Notes      |
| ---------- | ------- | ----------------------------------------------------------------------- | ---------- |
| **title**  | **str** | A human readable documentation for the problem type                     |
| **status** | **int** | The HTTP status code                                                    |
| **detail** | **str** | A human readable explanation specific to this occurrence of the problem | [optional] |
| **type**   | **str** | An absolute URI that identifies the problem type                        | [optional] |

## Example

```python
from openchallenges_api_client.models.basic_error import BasicError

# TODO update the JSON string below
json = "{}"
# create an instance of BasicError from a JSON string
basic_error_instance = BasicError.from_json(json)
# print the JSON string representation of the object
print(BasicError.to_json())

# convert the object into a dict
basic_error_dict = basic_error_instance.to_dict()
# create an instance of BasicError from a dict
basic_error_from_dict = BasicError.from_dict(basic_error_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
