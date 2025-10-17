# MintInternalToken200Response

## Properties

| Name             | Type    | Description | Notes |
| ---------------- | ------- | ----------- | ----- |
| **access_token** | **str** |             |
| **token_type**   | **str** |             |
| **expires_in**   | **int** |             |

## Example

```python
from bixarena_api_client.models.mint_internal_token200_response import MintInternalToken200Response

# TODO update the JSON string below
json = "{}"
# create an instance of MintInternalToken200Response from a JSON string
mint_internal_token200_response_instance = MintInternalToken200Response.from_json(json)
# print the JSON string representation of the object
print(MintInternalToken200Response.to_json())

# convert the object into a dict
mint_internal_token200_response_dict = mint_internal_token200_response_instance.to_dict()
# create an instance of MintInternalToken200Response from a dict
mint_internal_token200_response_from_dict = MintInternalToken200Response.from_dict(mint_internal_token200_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
