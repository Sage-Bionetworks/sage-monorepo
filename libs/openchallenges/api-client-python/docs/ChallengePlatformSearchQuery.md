# ChallengePlatformSearchQuery

A challenge platform search query.

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**page_number** | **int** | The page number. | [optional] [default to 0]
**page_size** | **int** | The number of items in a single page. | [optional] [default to 100]
**sort** | [**ChallengePlatformSort**](ChallengePlatformSort.md) |  | [optional] 
**direction** | [**ChallengePlatformDirection**](ChallengePlatformDirection.md) |  | [optional] 
**search_terms** | **str** | A string of search terms used to filter the results. | [optional] 

## Example

```python
from openchallenges_client.models.challenge_platform_search_query import ChallengePlatformSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengePlatformSearchQuery from a JSON string
challenge_platform_search_query_instance = ChallengePlatformSearchQuery.from_json(json)
# print the JSON string representation of the object
print ChallengePlatformSearchQuery.to_json()

# convert the object into a dict
challenge_platform_search_query_dict = challenge_platform_search_query_instance.to_dict()
# create an instance of ChallengePlatformSearchQuery from a dict
challenge_platform_search_query_form_dict = challenge_platform_search_query.from_dict(challenge_platform_search_query_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


