# LoginRequest

## Properties

| Name         | Type    | Description       | Notes |
| ------------ | ------- | ----------------- | ----- |
| **username** | **str** | Username or email |
| **password** | **str** | User password     |

## Example

```python
from openchallenges_api_client_python.models.login_request import LoginRequest

# TODO update the JSON string below
json = "{}"
# create an instance of LoginRequest from a JSON string
login_request_instance = LoginRequest.from_json(json)
# print the JSON string representation of the object
print(LoginRequest.to_json())

# convert the object into a dict
login_request_dict = login_request_instance.to_dict()
# create an instance of LoginRequest from a dict
login_request_from_dict = LoginRequest.from_dict(login_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
