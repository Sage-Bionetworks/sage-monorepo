# SimpleChallengePlatform

A simple challenge platform.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** | The unique identifier of a challenge platform. | 
**slug** | **str** | The slug of the challenge platform. | 
**name** | **str** | The name of the challenge platform. | 

## Example

```python
from openchallenges_api_client_python.models.simple_challenge_platform import SimpleChallengePlatform

# TODO update the JSON string below
json = "{}"
# create an instance of SimpleChallengePlatform from a JSON string
simple_challenge_platform_instance = SimpleChallengePlatform.from_json(json)
# print the JSON string representation of the object
print(SimpleChallengePlatform.to_json())

# convert the object into a dict
simple_challenge_platform_dict = simple_challenge_platform_instance.to_dict()
# create an instance of SimpleChallengePlatform from a dict
simple_challenge_platform_from_dict = SimpleChallengePlatform.from_dict(simple_challenge_platform_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


