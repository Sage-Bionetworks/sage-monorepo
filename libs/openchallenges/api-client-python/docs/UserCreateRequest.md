# UserCreateRequest

The information required to create a user account

## Properties

| Name           | Type    | Description       | Notes      |
| -------------- | ------- | ----------------- | ---------- |
| **login**      | **str** |                   |
| **email**      | **str** | An email address. |
| **password**   | **str** |                   |
| **name**       | **str** |                   | [optional] |
| **avatar_url** | **str** |                   | [optional] |
| **bio**        | **str** |                   | [optional] |

## Example

```python
from openchallenges_client.models.user_create_request import UserCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of UserCreateRequest from a JSON string
user_create_request_instance = UserCreateRequest.from_json(json)
# print the JSON string representation of the object
print(UserCreateRequest.to_json())

# convert the object into a dict
user_create_request_dict = user_create_request_instance.to_dict()
# create an instance of UserCreateRequest from a dict
user_create_request_from_dict = UserCreateRequest.from_dict(user_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
