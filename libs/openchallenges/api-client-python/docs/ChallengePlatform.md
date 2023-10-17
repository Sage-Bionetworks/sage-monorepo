# ChallengePlatform

A challenge platform

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** | The unique identifier of a challenge platform. | 
**slug** | **str** | The slug of the challenge platform. | 
**name** | **str** | The name of the challenge platform. | 
**avatar_url** | **str** |  | 
**website_url** | **str** |  | 
**created_at** | **datetime** |  | 
**updated_at** | **datetime** |  | 

## Example

```python
from openchallenges_client.models.challenge_platform import ChallengePlatform

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengePlatform from a JSON string
challenge_platform_instance = ChallengePlatform.from_json(json)
# print the JSON string representation of the object
print ChallengePlatform.to_json()

# convert the object into a dict
challenge_platform_dict = challenge_platform_instance.to_dict()
# create an instance of ChallengePlatform from a dict
challenge_platform_form_dict = challenge_platform.from_dict(challenge_platform_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


