# UserInfo

OIDC-compliant user information response

## Properties

| Name                   | Type          | Description                                 | Notes      |
| ---------------------- | ------------- | ------------------------------------------- | ---------- |
| **sub**                | **str**       | Subject identifier - the Synapse user ID    |
| **preferred_username** | **str**       | Preferred username for display              | [optional] |
| **email**              | **str**       | User&#39;s email address                    | [optional] |
| **email_verified**     | **bool**      | Whether the email address has been verified | [optional] |
| **roles**              | **List[str]** | User roles assigned within BixArena         | [optional] |

## Example

```python
from bixarena_api_client.models.user_info import UserInfo

# TODO update the JSON string below
json = "{}"
# create an instance of UserInfo from a JSON string
user_info_instance = UserInfo.from_json(json)
# print the JSON string representation of the object
print(UserInfo.to_json())

# convert the object into a dict
user_info_dict = user_info_instance.to_dict()
# create an instance of UserInfo from a dict
user_info_from_dict = UserInfo.from_dict(user_info_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
