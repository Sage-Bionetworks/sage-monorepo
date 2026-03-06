# BattleValidationResponse

## Properties

| Name              | Type         | Description                                               | Notes      |
| ----------------- | ------------ | --------------------------------------------------------- | ---------- |
| **id**            | **str**      |                                                           |
| **battle_id**     | **str**      |                                                           |
| **method**        | **str**      |                                                           |
| **confidence**    | **float**    |                                                           |
| **is_biomedical** | **bool**     |                                                           |
| **validated_by**  | **str**      | User ID of the validator (null for automated validations) | [optional] |
| **reason**        | **str**      | Optional reason for the validation decision               | [optional] |
| **created_at**    | **datetime** |                                                           |

## Example

```python
from bixarena_api_client.models.battle_validation_response import BattleValidationResponse

# TODO update the JSON string below
json = "{}"
# create an instance of BattleValidationResponse from a JSON string
battle_validation_response_instance = BattleValidationResponse.from_json(json)
# print the JSON string representation of the object
print(BattleValidationResponse.to_json())

# convert the object into a dict
battle_validation_response_dict = battle_validation_response_instance.to_dict()
# create an instance of BattleValidationResponse from a dict
battle_validation_response_from_dict = BattleValidationResponse.from_dict(battle_validation_response_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
