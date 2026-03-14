# BattleValidationCreateRequest

Request to manually validate or invalidate a battle.

## Properties

| Name              | Type     | Description                                                  | Notes      |
| ----------------- | -------- | ------------------------------------------------------------ | ---------- |
| **is_biomedical** | **bool** | Whether the admin considers this battle biomedically related |
| **reason**        | **str**  | Optional reason for the validation decision                  | [optional] |

## Example

```python
from bixarena_api_client.models.battle_validation_create_request import BattleValidationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleValidationCreateRequest from a JSON string
battle_validation_create_request_instance = BattleValidationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleValidationCreateRequest.to_json())

# convert the object into a dict
battle_validation_create_request_dict = battle_validation_create_request_instance.to_dict()
# create an instance of BattleValidationCreateRequest from a dict
battle_validation_create_request_from_dict = BattleValidationCreateRequest.from_dict(battle_validation_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
