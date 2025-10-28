# Callback200Response

## Properties

| Name       | Type    | Description | Notes      |
| ---------- | ------- | ----------- | ---------- |
| **status** | **str** |             | [optional] |

## Example

```python
from bixarena_api_client.models.callback200_response import Callback200Response

# TODO update the JSON string below
json = "{}"
# create an instance of Callback200Response from a JSON string
callback200_response_instance = Callback200Response.from_json(json)
# print the JSON string representation of the object
print(Callback200Response.to_json())

# convert the object into a dict
callback200_response_dict = callback200_response_instance.to_dict()
# create an instance of Callback200Response from a dict
callback200_response_from_dict = Callback200Response.from_dict(callback200_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
