# UserProfile

## Properties

| Name           | Type                                | Description                                      | Notes      |
| -------------- | ----------------------------------- | ------------------------------------------------ | ---------- |
| **id**         | **str**                             | Unique user identifier                           |
| **username**   | **str**                             | User&#39;s username                              |
| **email**      | **str**                             | User&#39;s email address                         |
| **first_name** | **str**                             | User&#39;s first name                            | [optional] |
| **last_name**  | **str**                             | User&#39;s last name                             | [optional] |
| **role**       | [**UserRole**](UserRole.md)         |                                                  |
| **scopes**     | [**List[AuthScope]**](AuthScope.md) | User&#39;s authorized scopes/permissions         | [optional] |
| **avatar_url** | **str**                             | URL to user&#39;s avatar image                   | [optional] |
| **bio**        | **str**                             | User&#39;s biography or description              | [optional] |
| **website**    | **str**                             | User&#39;s website URL                           | [optional] |
| **created_at** | **datetime**                        | Timestamp when the user account was created      |
| **updated_at** | **datetime**                        | Timestamp when the user profile was last updated | [optional] |

## Example

```python
from openchallenges_api_client.models.user_profile import UserProfile

# TODO update the JSON string below
json = "{}"
# create an instance of UserProfile from a JSON string
user_profile_instance = UserProfile.from_json(json)
# print the JSON string representation of the object
print(UserProfile.to_json())

# convert the object into a dict
user_profile_dict = user_profile_instance.to_dict()
# create an instance of UserProfile from a dict
user_profile_from_dict = UserProfile.from_dict(user_profile_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
