# ApiKey

## Properties

| Name             | Type         | Description                                          | Notes      |
| ---------------- | ------------ | ---------------------------------------------------- | ---------- |
| **id**           | **str**      | API key ID                                           | [optional] |
| **name**         | **str**      | Human-readable name for the API key                  | [optional] |
| **prefix**       | **str**      | First 8 characters of the API key for identification | [optional] |
| **created_at**   | **datetime** | When the API key was created                         | [optional] |
| **expires_at**   | **datetime** | When the API key expires (null if no expiration)     | [optional] |
| **last_used_at** | **datetime** | When the API key was last used (null if never used)  | [optional] |

## Example

```python
from openchallenges_api_client_python.models.api_key import ApiKey

# TODO update the JSON string below
json = "{}"
# create an instance of ApiKey from a JSON string
api_key_instance = ApiKey.from_json(json)
# print the JSON string representation of the object
print(ApiKey.to_json())

# convert the object into a dict
api_key_dict = api_key_instance.to_dict()
# create an instance of ApiKey from a dict
api_key_from_dict = ApiKey.from_dict(api_key_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
