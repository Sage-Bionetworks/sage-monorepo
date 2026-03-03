# BattleEvaluationCreateRequest

The information used to create a new battle evaluation.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**outcome** | [**BattleEvaluationOutcome**](BattleEvaluationOutcome.md) |  | 

## Example

```python
from bixarena_api_client.models.battle_evaluation_create_request import BattleEvaluationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of BattleEvaluationCreateRequest from a JSON string
battle_evaluation_create_request_instance = BattleEvaluationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(BattleEvaluationCreateRequest.to_json())

# convert the object into a dict
battle_evaluation_create_request_dict = battle_evaluation_create_request_instance.to_dict()
# create an instance of BattleEvaluationCreateRequest from a dict
battle_evaluation_create_request_from_dict = BattleEvaluationCreateRequest.from_dict(battle_evaluation_create_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


