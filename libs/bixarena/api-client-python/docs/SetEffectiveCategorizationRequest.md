# SetEffectiveCategorizationRequest

Request to set or clear the effective categorization for an example prompt or battle.

## Properties

| Name                  | Type    | Description                                                                                          | Notes      |
| --------------------- | ------- | ---------------------------------------------------------------------------------------------------- | ---------- |
| **categorization_id** | **str** | ID of the categorization row to set as effective. Set to null to clear the effective categorization. | [optional] |

## Example

```python
from bixarena_api_client.models.set_effective_categorization_request import SetEffectiveCategorizationRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SetEffectiveCategorizationRequest from a JSON string
set_effective_categorization_request_instance = SetEffectiveCategorizationRequest.from_json(json)
# print the JSON string representation of the object
print(SetEffectiveCategorizationRequest.to_json())

# convert the object into a dict
set_effective_categorization_request_dict = set_effective_categorization_request_instance.to_dict()
# create an instance of SetEffectiveCategorizationRequest from a dict
set_effective_categorization_request_from_dict = SetEffectiveCategorizationRequest.from_dict(set_effective_categorization_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
