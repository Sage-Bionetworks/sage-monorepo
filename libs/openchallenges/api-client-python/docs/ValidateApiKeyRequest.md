# ValidateApiKeyRequest

## Properties

| Name        | Type    | Description             | Notes |
| ----------- | ------- | ----------------------- | ----- |
| **api_key** | **str** | The API key to validate |

## Example

```python
from openchallenges_api_client_python.models.validate_api_key_request import ValidateApiKeyRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ValidateApiKeyRequest from a JSON string
validate_api_key_request_instance = ValidateApiKeyRequest.from_json(json)
# print the JSON string representation of the object
print(ValidateApiKeyRequest.to_json())

# convert the object into a dict
validate_api_key_request_dict = validate_api_key_request_instance.to_dict()
# create an instance of ValidateApiKeyRequest from a dict
validate_api_key_request_from_dict = ValidateApiKeyRequest.from_dict(validate_api_key_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
