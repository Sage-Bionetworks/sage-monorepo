# OidcCallback200Response

## Properties

| Name       | Type    | Description | Notes      |
| ---------- | ------- | ----------- | ---------- |
| **status** | **str** |             | [optional] |

## Example

```python
from bixarena_api_client.models.oidc_callback200_response import OidcCallback200Response

# TODO update the JSON string below
json = "{}"
# create an instance of OidcCallback200Response from a JSON string
oidc_callback200_response_instance = OidcCallback200Response.from_json(json)
# print the JSON string representation of the object
print(OidcCallback200Response.to_json())

# convert the object into a dict
oidc_callback200_response_dict = oidc_callback200_response_instance.to_dict()
# create an instance of OidcCallback200Response from a dict
oidc_callback200_response_from_dict = OidcCallback200Response.from_dict(oidc_callback200_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
