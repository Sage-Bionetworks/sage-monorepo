# UpdateUserProfileRequest

## Properties

| Name           | Type    | Description                         | Notes      |
| -------------- | ------- | ----------------------------------- | ---------- |
| **first_name** | **str** | User&#39;s first name               | [optional] |
| **last_name**  | **str** | User&#39;s last name                | [optional] |
| **bio**        | **str** | User&#39;s biography or description | [optional] |
| **website**    | **str** | User&#39;s website URL              | [optional] |
| **avatar_url** | **str** | URL to user&#39;s avatar image      | [optional] |

## Example

```python
from openchallenges_api_client.models.update_user_profile_request import UpdateUserProfileRequest

# TODO update the JSON string below
json = "{}"
# create an instance of UpdateUserProfileRequest from a JSON string
update_user_profile_request_instance = UpdateUserProfileRequest.from_json(json)
# print the JSON string representation of the object
print(UpdateUserProfileRequest.to_json())

# convert the object into a dict
update_user_profile_request_dict = update_user_profile_request_instance.to_dict()
# create an instance of UpdateUserProfileRequest from a dict
update_user_profile_request_from_dict = UpdateUserProfileRequest.from_dict(update_user_profile_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
