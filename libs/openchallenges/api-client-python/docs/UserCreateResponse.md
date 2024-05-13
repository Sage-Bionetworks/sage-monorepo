# UserCreateResponse

The response returned after the creation of the user

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** | The unique identifier of an account | 

## Example

```python
from openchallenges_client.models.user_create_response import UserCreateResponse

# TODO update the JSON string below
json = "{}"
# create an instance of UserCreateResponse from a JSON string
user_create_response_instance = UserCreateResponse.from_json(json)
# print the JSON string representation of the object
print(UserCreateResponse.to_json())

# convert the object into a dict
user_create_response_dict = user_create_response_instance.to_dict()
# create an instance of UserCreateResponse from a dict
user_create_response_from_dict = UserCreateResponse.from_dict(user_create_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


