# SetEffectiveValidationRequest

Request to set or clear the effective validation for a battle.

## Properties

| Name              | Type    | Description                                                                                     | Notes      |
| ----------------- | ------- | ----------------------------------------------------------------------------------------------- | ---------- |
| **validation_id** | **str** | ID of the battle validation to set as effective. Set to null to clear the effective validation. | [optional] |

## Example

```python
from bixarena_api_client.models.set_effective_validation_request import SetEffectiveValidationRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SetEffectiveValidationRequest from a JSON string
set_effective_validation_request_instance = SetEffectiveValidationRequest.from_json(json)
# print the JSON string representation of the object
print(SetEffectiveValidationRequest.to_json())

# convert the object into a dict
set_effective_validation_request_dict = set_effective_validation_request_instance.to_dict()
# create an instance of SetEffectiveValidationRequest from a dict
set_effective_validation_request_from_dict = SetEffectiveValidationRequest.from_dict(set_effective_validation_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
