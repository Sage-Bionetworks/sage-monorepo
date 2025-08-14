# ChallengeCreateRequest

The information used to create a challenge

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**slug** | **str** | The unique slug of the challenge. | 
**name** | **str** | The name of the challenge. | 
**headline** | **str** | The headline of the challenge. | [optional] 
**description** | **str** | The description of the challenge. | [optional] 
**doi** | **str** | The DOI of the challenge. | [optional] 
**status** | [**ChallengeStatus**](ChallengeStatus.md) |  | 
**platform_id** | **int** | The unique identifier of a challenge platform. | [optional] 
**website_url** | **str** | A URL to the website or image. | 
**avatar_url** | **str** | A URL to the website or image. | [optional] 
**start_date** | **date** | The start date of the challenge. | [optional] 
**end_date** | **date** | The end date of the challenge. | [optional] 
**incentives** | [**List[ChallengeIncentive]**](ChallengeIncentive.md) |  | [optional] 
**submission_types** | [**List[ChallengeSubmissionType]**](ChallengeSubmissionType.md) |  | [optional] 
**categories** | [**List[ChallengeCategory]**](ChallengeCategory.md) |  | [optional] 
**input_data_types** | **List[int]** |  | [optional] 
**operation** | **int** | The unique identifier of the EDAM concept. | [optional] 

## Example

```python
from openchallenges_api_client_python.models.challenge_create_request import ChallengeCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeCreateRequest from a JSON string
challenge_create_request_instance = ChallengeCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengeCreateRequest.to_json())

# convert the object into a dict
challenge_create_request_dict = challenge_create_request_instance.to_dict()
# create an instance of ChallengeCreateRequest from a dict
challenge_create_request_from_dict = ChallengeCreateRequest.from_dict(challenge_create_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


