# ValidateApiKeyResponse

## Properties

| Name         | Type          | Description                                          | Notes      |
| ------------ | ------------- | ---------------------------------------------------- | ---------- |
| **valid**    | **bool**      | Whether the API key is valid                         | [optional] |
| **user_id**  | **str**       | ID of the user who owns this API key (only if valid) | [optional] |
| **username** | **str**       | Username of the API key owner (only if valid)        | [optional] |
| **role**     | **str**       | Role of the API key owner (only if valid)            | [optional] |
| **scopes**   | **List[str]** | Permissions granted to this API key (only if valid)  | [optional] |

## Example

```python
from openchallenges_api_client_python.models.validate_api_key_response import ValidateApiKeyResponse

# TODO update the JSON string below
json = "{}"
# create an instance of ValidateApiKeyResponse from a JSON string
validate_api_key_response_instance = ValidateApiKeyResponse.from_json(json)
# print the JSON string representation of the object
print(ValidateApiKeyResponse.to_json())

# convert the object into a dict
validate_api_key_response_dict = validate_api_key_response_instance.to_dict()
# create an instance of ValidateApiKeyResponse from a dict
validate_api_key_response_from_dict = ValidateApiKeyResponse.from_dict(validate_api_key_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
