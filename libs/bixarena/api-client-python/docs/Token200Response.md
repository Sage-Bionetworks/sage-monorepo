# Token200Response

## Properties

| Name             | Type    | Description | Notes |
| ---------------- | ------- | ----------- | ----- |
| **access_token** | **str** |             |
| **token_type**   | **str** |             |
| **expires_in**   | **int** |             |

## Example

```python
from bixarena_api_client.models.token200_response import Token200Response

# TODO update the JSON string below
json = "{}"
# create an instance of Token200Response from a JSON string
token200_response_instance = Token200Response.from_json(json)
# print the JSON string representation of the object
print(Token200Response.to_json())

# convert the object into a dict
token200_response_dict = token200_response_instance.to_dict()
# create an instance of Token200Response from a dict
token200_response_from_dict = Token200Response.from_dict(token200_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
