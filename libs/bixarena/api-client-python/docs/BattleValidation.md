# BattleValidation

## Properties

| Name              | Type      | Description                                                                                                    | Notes |
| ----------------- | --------- | -------------------------------------------------------------------------------------------------------------- | ----- |
| **confidence**    | **float** | Confidence score indicating biomedical relevance (0.0 &#x3D; not biomedical, 1.0 &#x3D; definitely biomedical) |
| **is_biomedical** | **bool**  | Whether the battle conversation is considered biomedically related (confidence &gt;&#x3D; 0.5)                 |
| **method**        | **str**   | The validation method used (e.g. &#39;openrouter-haiku-v1&#39;)                                                |

## Example

```python
from bixarena_api_client.models.battle_validation import BattleValidation

# TODO update the JSON string below
json = "{}"
# create an instance of BattleValidation from a JSON string
battle_validation_instance = BattleValidation.from_json(json)
# print the JSON string representation of the object
print(BattleValidation.to_json())

# convert the object into a dict
battle_validation_dict = battle_validation_instance.to_dict()
# create an instance of BattleValidation from a dict
battle_validation_from_dict = BattleValidation.from_dict(battle_validation_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
