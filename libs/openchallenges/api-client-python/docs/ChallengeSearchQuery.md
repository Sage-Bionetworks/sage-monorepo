# ChallengeSearchQuery

A challenge search query.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**page_number** | **int** | The page number. | [optional] [default to 0]
**page_size** | **int** | The number of items in a single page. | [optional] [default to 100]
**sort** | [**ChallengeSort**](ChallengeSort.md) |  | [optional] [default to ChallengeSort.RELEVANCE]
**sort_seed** | **int** | The seed that initializes the random sorter. | [optional] 
**direction** | [**ChallengeDirection**](ChallengeDirection.md) |  | [optional] 
**incentives** | [**List[ChallengeIncentive]**](ChallengeIncentive.md) | An array of challenge incentive types used to filter the results. | [optional] 
**min_start_date** | **date** | Keep the challenges that start at this date or later. | [optional] 
**max_start_date** | **date** | Keep the challenges that start at this date or sooner. | [optional] 
**platforms** | **List[str]** | An array of challenge platform ids used to filter the results. | [optional] 
**organizations** | **List[int]** | An array of organization ids used to filter the results. | [optional] 
**status** | [**List[ChallengeStatus]**](ChallengeStatus.md) | An array of challenge status used to filter the results. | [optional] 
**submission_types** | [**List[ChallengeSubmissionType]**](ChallengeSubmissionType.md) | An array of challenge submission types used to filter the results. | [optional] 
**input_data_types** | **List[int]** | An array of EDAM concept ID used to filter the results. | [optional] 
**operations** | **List[int]** | An array of EDAM concept ID used to filter the results. | [optional] 
**categories** | [**List[ChallengeCategory]**](ChallengeCategory.md) | The array of challenge categories used to filter the results. | [optional] 
**search_terms** | **str** | A string of search terms used to filter the results. | [optional] 

## Example

```python
from openchallenges_api_client_python.models.challenge_search_query import ChallengeSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeSearchQuery from a JSON string
challenge_search_query_instance = ChallengeSearchQuery.from_json(json)
# print the JSON string representation of the object
print(ChallengeSearchQuery.to_json())

# convert the object into a dict
challenge_search_query_dict = challenge_search_query_instance.to_dict()
# create an instance of ChallengeSearchQuery from a dict
challenge_search_query_from_dict = ChallengeSearchQuery.from_dict(challenge_search_query_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


