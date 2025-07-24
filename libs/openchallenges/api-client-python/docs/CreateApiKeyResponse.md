# CreateApiKeyResponse

## Properties

| Name           | Type         | Description                                          | Notes      |
| -------------- | ------------ | ---------------------------------------------------- | ---------- |
| **id**         | **str**      | API key ID                                           | [optional] |
| **key**        | **str**      | The actual API key (only returned on creation)       | [optional] |
| **name**       | **str**      | Human-readable name for the API key                  | [optional] |
| **prefix**     | **str**      | First 8 characters of the API key for identification | [optional] |
| **created_at** | **datetime** | When the API key was created                         | [optional] |
| **expires_at** | **datetime** | When the API key expires (null if no expiration)     | [optional] |

## Example

```python
from openchallenges_api_client_python.models.create_api_key_response import CreateApiKeyResponse

# TODO update the JSON string below
json = "{}"
# create an instance of CreateApiKeyResponse from a JSON string
create_api_key_response_instance = CreateApiKeyResponse.from_json(json)
# print the JSON string representation of the object
print(CreateApiKeyResponse.to_json())

# convert the object into a dict
create_api_key_response_dict = create_api_key_response_instance.to_dict()
# create an instance of CreateApiKeyResponse from a dict
create_api_key_response_from_dict = CreateApiKeyResponse.from_dict(create_api_key_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
