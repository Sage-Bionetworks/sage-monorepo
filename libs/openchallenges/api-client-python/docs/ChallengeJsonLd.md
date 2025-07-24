# ChallengeJsonLd

A challenge

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** | The unique identifier of the challenge. | 
**slug** | **str** | The unique slug of the challenge. | 
**name** | **str** | The name of the challenge. | 
**headline** | **str** | The headline of the challenge. | [optional] 
**description** | **str** | The description of the challenge. | 
**doi** | **str** | The DOI of the challenge. | [optional] 
**status** | [**ChallengeStatus**](ChallengeStatus.md) |  | 
**platform** | [**SimpleChallengePlatform**](SimpleChallengePlatform.md) |  | [optional] 
**website_url** | **str** | A URL to the website or image. | [optional] 
**avatar_url** | **str** | A URL to the website or image. | [optional] 
**incentives** | [**List[ChallengeIncentive]**](ChallengeIncentive.md) |  | 
**submission_types** | [**List[ChallengeSubmissionType]**](ChallengeSubmissionType.md) |  | 
**input_data_types** | [**List[EdamConcept]**](EdamConcept.md) |  | [optional] 
**categories** | [**List[ChallengeCategory]**](ChallengeCategory.md) |  | 
**start_date** | **date** | The start date of the challenge. | [optional] 
**end_date** | **date** | The end date of the challenge. | [optional] 
**starred_count** | **int** | The number of times the challenge has been starred by users. | [default to 0]
**operation** | [**EdamConcept**](EdamConcept.md) |  | [optional] 
**created_at** | **datetime** | Datetime when the object was added to the database. | 
**updated_at** | **datetime** | Datetime when the object was last modified in the database. | 
**context** | **str** |  | 
**id** | **str** |  | 
**type** | **str** |  | 

## Example

```python
from openchallenges_api_client_python.models.challenge_json_ld import ChallengeJsonLd

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeJsonLd from a JSON string
challenge_json_ld_instance = ChallengeJsonLd.from_json(json)
# print the JSON string representation of the object
print(ChallengeJsonLd.to_json())

# convert the object into a dict
challenge_json_ld_dict = challenge_json_ld_instance.to_dict()
# create an instance of ChallengeJsonLd from a dict
challenge_json_ld_from_dict = ChallengeJsonLd.from_dict(challenge_json_ld_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


