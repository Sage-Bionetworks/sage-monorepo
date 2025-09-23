# CreateApiKeyRequest

## Properties

| Name           | Type    | Description                                                                   | Notes      |
| -------------- | ------- | ----------------------------------------------------------------------------- | ---------- |
| **name**       | **str** | Human-readable name for the API key                                           |
| **expires_in** | **int** | Number of days until the API key expires (optional, default is no expiration) | [optional] |

## Example

```python
from openchallenges_api_client.models.create_api_key_request import CreateApiKeyRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CreateApiKeyRequest from a JSON string
create_api_key_request_instance = CreateApiKeyRequest.from_json(json)
# print the JSON string representation of the object
print(CreateApiKeyRequest.to_json())

# convert the object into a dict
create_api_key_request_dict = create_api_key_request_instance.to_dict()
# create an instance of CreateApiKeyRequest from a dict
create_api_key_request_from_dict = CreateApiKeyRequest.from_dict(create_api_key_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
